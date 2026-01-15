package com.example.rutasbus.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.rutasbus.R
import com.google.firebase.auth.FirebaseAuth

class Register : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_register)

        auth = FirebaseAuth.getInstance()

        val btnLogin : TextView = findViewById(R.id.txtlogin)
        val btnRegister : Button = findViewById(R.id.btn_register)
        val emailEditText: EditText = findViewById(R.id.email_register)
        val passwordEditText: EditText = findViewById(R.id.pass_register)

        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            register(email, password)
        }
    }

    private fun register(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}