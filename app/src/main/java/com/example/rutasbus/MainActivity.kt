package com.example.rutasbus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.example.rutasbus.authentication.Login
import com.example.rutasbus.pantallasinfo.InformacionTwo


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_carga)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val infoShown = prefs.getBoolean("infoShown", false)

            val intent = if (!infoShown) {
                Intent(this, InformacionTwo::class.java)
            } else {
                Intent(this, Login::class.java)
            }
            startActivity(intent)
            finish()
        }, 1500)

    }
}