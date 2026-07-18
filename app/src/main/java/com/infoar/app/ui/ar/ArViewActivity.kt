package com.infoar.app.ui.ar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.core.ArCoreApk
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.utp.parcial2_proyecto.R
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode

class ArViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESC = "extra_description"
        const val EXTRA_URL = "extra_model_url"
        private const val TAG = "ArViewActivity"
    }

    private var sceneView: ARSceneView? = null
    private var modelUrl: String? = null
    private var isModelPlaced = false

    private var arContainer: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_view)

        val name = intent.getStringExtra(EXTRA_NAME) ?: "Objeto 3D"
        val desc = intent.getStringExtra(EXTRA_DESC) ?: ""
        modelUrl = intent.getStringExtra(EXTRA_URL)?.trim()

        if (modelUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No se recibió la ruta del modelo", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.tvArName).text = name
        findViewById<TextView>(R.id.tvArDescription).text = desc
        findViewById<Button>(R.id.btnArDetail).setOnClickListener { finish() }
        findViewById<FloatingActionButton>(R.id.fabArBack).setOnClickListener { finish() }

        arContainer = findViewById(R.id.arContainer)

        // Verificar disponibilidad de ARCore
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        if (!availability.isSupported && !availability.isTransient) {
            Toast.makeText(this, "ARCore no es compatible con este dispositivo", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Retraso de seguridad para que ScanFragment libere la cámara completamente
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    initSceneViewProgrammatically()
                } else {
                    Toast.makeText(this, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }, 2000)
    }

    private fun initSceneViewProgrammatically() {
        try {
            val sv = ARSceneView(this)
            sv.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            arContainer?.addView(sv)
            sceneView = sv

            setupAR()
        } catch (e: Exception) {
            Log.e(TAG, "Error al instanciar SceneView: ${e.message}")
            Toast.makeText(this, "Error al iniciar AR: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupAR() {
        try {
            sceneView?.apply {
                this.lifecycle = this@ArViewActivity.lifecycle
                planeRenderer.isVisible = true
                
                onTouchEvent = { motionEvent, _ ->
                    if (motionEvent.action == MotionEvent.ACTION_UP && !isModelPlaced) {
                        try {
                            val hitResult = hitTestAR(
                                xPx = motionEvent.x,
                                yPx = motionEvent.y,
                                planeTypes = setOf(Plane.Type.HORIZONTAL_UPWARD_FACING)
                            )
                            hitResult?.let { placeModel(it) }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error en toque AR: ${e.message}")
                        }
                    }
                    true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupAR: ${e.message}", e)
            Toast.makeText(this, "El servicio de AR está tardando en responder. Por favor, reintenta en un momento.", Toast.LENGTH_LONG).show()
            // No hacemos finish inmediato para dejar que el usuario vea el mensaje
        }
    }

    override fun onResume() {
        super.onResume()
        // SceneView maneja su propio ciclo de vida si se le asigna sceneView.lifecycle = lifecycle
        // No es necesario llamar a resume() o pause() manualmente en la mayoría de las versiones de SceneView
    }

    override fun onPause() {
        super.onPause()
    }

    private fun placeModel(hitResult: HitResult) {
        val url = modelUrl ?: return
        Toast.makeText(this, "Cargando modelo...", Toast.LENGTH_SHORT).show()
        
        sceneView?.let { sv ->
            sv.modelLoader.loadModelInstanceAsync(url) { modelInstance ->
                if (modelInstance != null && this@ArViewActivity.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                    try {
                        val anchor = hitResult.createAnchor()
                        val anchorNode = AnchorNode(sv.engine, anchor)
                        val modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            autoAnimate = true
                        ).apply {
                            isEditable = true
                            isPositionEditable = true
                            isRotationEditable = true
                            isScaleEditable = true
                        }
                        anchorNode.addChildNode(modelNode)
                        sv.addChildNode(anchorNode)
                        isModelPlaced = true
                        
                        runOnUiThread {
                            Toast.makeText(this, "¡Éxito! Interactúa con el objeto.", Toast.LENGTH_LONG).show()
                            sv.planeRenderer.isVisible = false 
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al crear ancla o colocar modelo", e)
                        runOnUiThread {
                            Toast.makeText(this, "Error al colocar el modelo: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else if (modelInstance == null) {
                    runOnUiThread {
                        Toast.makeText(this, "Error al descargar el modelo", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            sceneView?.destroy()
            sceneView = null
        } catch (e: Exception) { }
        super.onDestroy()
    }
}
