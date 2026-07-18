package com.infoar.app.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.infoar.app.data.local.db.AppDatabase
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.data.repository.HistoryRepository
import com.infoar.app.ui.ar.ArViewActivity
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private var cameraExecutor: ExecutorService? = null
    private var isScanning = true
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor!!, QRAnalyzer { qrCode ->
                            if (isScanning) {
                                isScanning = false
                                processQRCode(qrCode)
                            }
                        })
                    }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e("ScanFragment", "Error cámara", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processQRCode(qrCode: String) {
        db.collection("places").whereEqualTo("qrCode", qrCode.trim()).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val name = doc.getString("name") ?: "Lugar"
                    val desc = doc.getString("description") ?: ""
                    val url = doc.getString("model3dUrl") ?: ""
                    
                    if (url.isNotEmpty()) {
                        saveToHistoryAndNavigate(name, desc, url)
                    } else {
                        Toast.makeText(requireContext(), "Este lugar no tiene modelo 3D configurado", Toast.LENGTH_SHORT).show()
                        isScanning = true
                    }
                } else {
                    Toast.makeText(requireContext(), "Código QR no registrado en la base de datos", Toast.LENGTH_SHORT).show()
                    isScanning = true
                }
            }
            .addOnFailureListener { 
                Toast.makeText(requireContext(), "Error de conexión con la base de datos", Toast.LENGTH_SHORT).show()
                isScanning = true 
            }
    }

    private fun saveToHistoryAndNavigate(name: String, desc: String, url: String) {
        val activity = activity ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(activity.applicationContext)
            database.historyDao().insert(HistoryEntity(
                name = name,
                description = desc,
                date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                category = "Escaneado",
                model3dUrl = url
            ))
            
            withContext(Dispatchers.Main) {
                // UNBIND CAMERA BEFORE NAVIGATING TO AR
                try {
                    val cameraProvider = ProcessCameraProvider.getInstance(activity).get()
                    cameraProvider.unbindAll()
                } catch (e: Exception) {
                    Log.e("ScanFragment", "Error al desvincular cámara", e)
                }

                val intent = Intent(activity, ArViewActivity::class.java).apply {
                    putExtra(ArViewActivity.EXTRA_NAME, name)
                    putExtra(ArViewActivity.EXTRA_DESC, desc)
                    putExtra(ArViewActivity.EXTRA_URL, url)
                }
                startActivity(intent)
                delay(2000)
                isScanning = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor?.shutdown()
        cameraExecutor = null
    }

    private class QRAnalyzer(private val onQrDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val scanner = BarcodeScanning.getClient()
        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) { 
                            barcode.rawValue?.let { onQrDetected(it) } 
                        }
                    }
                    .addOnCompleteListener { imageProxy.close() }
            } else { imageProxy.close() }
        }
    }
}
