package com.example.rutasbus.data

data class Parada (
    val tipo: String = "",      // INICIO | INTERMEDIA | FIN
    val nombre: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)