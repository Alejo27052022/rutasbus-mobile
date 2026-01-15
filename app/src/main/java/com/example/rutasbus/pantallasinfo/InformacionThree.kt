package com.example.rutasbus.pantallasinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.rutasbus.R
import com.example.rutasbus.authentication.Login

class InformacionThree : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantallainfo3)

        val layout_pantallaInfo3 : ConstraintLayout = findViewById(R.id.layout_pantallainfo3)

        layout_pantallaInfo3.setOnClickListener {
            // Guardar el flag que ya se mostró la info
            val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("infoShown", true).apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}