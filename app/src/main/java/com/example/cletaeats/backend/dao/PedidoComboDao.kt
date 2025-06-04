package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.model.PedidoCombo
import com.example.cletaeats.backend.util.DatabaseHelper
import java.sql.ResultSet

class PedidoComboDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(pc: PedidoCombo): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("pedido_id", pc.pedidoId)
            put("combo_id", pc.comboId)
            put("cantidad", pc.cantidad)
        }
        return db.insert("PedidoCombo", null, values) > 0
    }

    fun obtenerPorPedido(pedidoId: Int): List<PedidoCombo> {
        val lista = mutableListOf<PedidoCombo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM PedidoCombo WHERE pedido_id = ?", arrayOf(pedidoId.toString()))
        while (cursor.moveToNext()) {
            lista.add(
                PedidoCombo(
                    id = cursor.getInt(0),
                    pedidoId = cursor.getInt(1),
                    comboId = cursor.getInt(2),
                    cantidad = cursor.getInt(3)
                )
            )
        }
        return lista
    }
}
