package com.example.cletaeats.backend.model

data class ClientePedidos(
    val clienteNombre: String,
    val cantidadPedidos: Int,
    val restaurantes: List<String>
)
