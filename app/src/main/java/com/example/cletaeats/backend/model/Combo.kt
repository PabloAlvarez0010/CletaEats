package com.example.cletaeats.backend.model

data class Combo(
    val id: Int = 0, // autogenerado
    val numero: Int, // del 1 al 9
    val precio: Double,
    val descripcion: String,
    val restauranteId: Int
)