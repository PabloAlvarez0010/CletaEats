package com.example.cletaeats.backend.model

data class Repartidor(
    val cedula: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val telefono: String,
    val tarjeta: String,
    val estado: String,              // "disponible" o "ocupado"
    val distanciaPedido: Double,
    val kmDiarios: Double,
    val costoKmHabil: Double,
    val costoKmFeriado: Double,
    val amonestaciones: Int
)