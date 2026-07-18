package com.infoar.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.utp.parcial2_proyecto.R

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<TextInputEditText>(R.id.etRegName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etRegEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etRegPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        
                        userId?.let {
                            db.collection("users").document(it).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                                    (activity as? AuthActivity)?.replaceFragment(LoginFragment())
                                }
                                .addOnFailureListener { e ->
                                    btnRegister.isEnabled = true
                                    Toast.makeText(requireContext(), "Error al guardar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        btnRegister.isEnabled = true
                        Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        view.findViewById<TextView>(R.id.tvBackToLogin).setOnClickListener {
            (activity as? AuthActivity)?.replaceFragment(LoginFragment())
        }
    }
}
