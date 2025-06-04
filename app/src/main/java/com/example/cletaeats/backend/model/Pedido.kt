package com.example.cletaeats.backend.model

data class Pedido(
    val id: Int = 0,
    val clienteId: String,
    val restauranteId: Int,
    val repartidorId: String?, // puede ser null si aún no está asignado
    val estado: String,        // "en preparación", "en camino", "suspendido", "entregado"
    val horaPedido: String,
    val horaEntrega: String?,
    val subtotal: Double,
    val costoTransporte: Double,
    val iva: Double,
    val total: Double
)