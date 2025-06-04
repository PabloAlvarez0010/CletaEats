package com.example.cletaeats.backend.model

data class Queja(
    val id: Int = 0,
    val repartidorId: String,
    val clienteId: String,
    val descripcion: String,
    val fecha: String
)