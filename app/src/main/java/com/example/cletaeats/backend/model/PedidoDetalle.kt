package com.example.cletaeats.backend.model

data class PedidoDetalle(
    val id: Int,
    val estado: String,
    val horaPedido: String,
    val horaEntrega: String?,
    val total: Double,
    val restaurante: String,
    val tipoComida: String,
    val combos: List<ComboDetalle>,
    val repartidorNombre: String? = null,
    val repartidorCedula: String? = null

)