package com.example.rutasbus.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.rutasbus.Inicio
import com.example.rutasbus.R
import com.google.firebase.auth.FirebaseAuth

class Login : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_login)

        auth = FirebaseAuth.getInstance()
        val btnLogin : Button = findViewById(R.id.btn_login)
        val btnRegister : TextView = findViewById(R.id.txtregister)
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)

        btnLogin.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            login(email, password)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }

    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Inicio::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}