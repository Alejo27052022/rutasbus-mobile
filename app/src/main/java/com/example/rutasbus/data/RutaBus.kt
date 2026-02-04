package com.example.rutasbus.data

data class RutaBus(
    val nombre: String = "",
    val paradas: Map<String, Parada> = emptyMap()
)