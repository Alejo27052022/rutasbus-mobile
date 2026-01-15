package com.example.rutasbus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rutasbus.ui.maps.MapsFragment
import com.example.rutasbus.ui.home.HomeFragment
import com.example.rutasbus.ui.rutas.RutasFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Inicio : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_inicio)

        // Inicializamos el BottomNavigationView
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)

        // Configuración inicial para cargar el fragmento de "Inicio"
        loadFragment(MapsFragment())

        // Listener para cambiar entre fragmentos cuando se selecciona un ítem del BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(MapsFragment()) // Fragment de Inicio
                    true
                }
                R.id.nav_rutas -> {
                    loadFragment(RutasFragment()) // Fragment de Rutas
                    true
                }
                else -> false
            }
        }
    }

    // Función para cargar un fragment en el contenedor
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
