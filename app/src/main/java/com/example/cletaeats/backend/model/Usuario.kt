package com.example.cletaeats.backend.model

data class Usuario(
    val id: Int = 0,
    val cedula: String,
    val clave: String,
    val rol: String // "cliente", "repartidor", "admin"
)