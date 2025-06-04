package com.example.cletaeats.backend.model

data class Restaurante(
    val id: Int = 0, // autogenerado por la BD
    val nombre: String,
    val cedulaJuridica: String,
    val direccion: String,
    val tipoComida: String
)