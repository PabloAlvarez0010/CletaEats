package com.example.cletaeats.backend.model

data class Cliente(
    val cedula: String,
    val nombre: String,
    val direccion: String,
    val tarjeta: String,
    val telefono: String,
    val correo: String,
    val estado: String // debe ser "activo" o "suspendido"
)