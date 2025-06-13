package com.example.cletaeats.fronted.ui.cliente

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.cletaeats.backend.model.Combo

class CarritoViewModel : ViewModel() {
    val carrito = mutableStateListOf<Pair<Combo, Int>>() // Combo con cantidad
    var restauranteIdActual: Int? = null

    fun agregarCombo(combo: Combo): Boolean {
        if (restauranteIdActual == null || restauranteIdActual == combo.restauranteId) {
            restauranteIdActual = combo.restauranteId
            val index = carrito.indexOfFirst { it.first.id == combo.id }
            if (index >= 0) {
                carrito[index] = carrito[index].copy(second = carrito[index].second + 1)
            } else {
                carrito.add(combo to 1)
            }
            return true
        }
        return false // Otro restaurante
    }
    fun eliminarCombo(combo: Combo) {
        val index = carrito.indexOfFirst { it.first.id == combo.id }
        if (index >= 0) {
            if (carrito[index].second > 1) {
                carrito[index] = carrito[index].copy(second = carrito[index].second - 1)
            } else {
                carrito.removeAt(index)
            }
        }
        // Si el carrito quedó vacío, limpiar restauranteIdActual también
        if (carrito.isEmpty()) {
            restauranteIdActual = null
        }
    }
    fun limpiar() {
        carrito.clear()
        restauranteIdActual = null
    }
}