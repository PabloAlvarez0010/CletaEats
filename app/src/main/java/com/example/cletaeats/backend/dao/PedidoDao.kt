package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.model.Pedido
import com.example.cletaeats.backend.util.DatabaseHelper
import java.sql.ResultSet

class PedidoDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(p: Pedido): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("cliente_id", p.clienteId)
            put("restaurante_id", p.restauranteId)
            put("repartidor_id", p.repartidorId)
            put("estado", p.estado)
            put("hora_pedido", p.horaPedido)
            put("hora_entrega", p.horaEntrega)
            put("subtotal", p.subtotal)
            put("costo_transporte", p.costoTransporte)
            put("iva", p.iva)
            put("total", p.total)
        }
        return db.insert("Pedido", null, values) > 0
    }

    fun obtenerTodos(): List<Pedido> {
        val lista = mutableListOf<Pedido>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Pedido", null)
        while (cursor.moveToNext()) {
            lista.add(
                Pedido(
                    id = cursor.getInt(0),
                    clienteId = cursor.getString(1),
                    restauranteId = cursor.getInt(2),
                    repartidorId = cursor.getString(3),
                    estado = cursor.getString(4),
                    horaPedido = cursor.getString(5),
                    horaEntrega = cursor.getString(6),
                    subtotal = cursor.getDouble(7),
                    costoTransporte = cursor.getDouble(8),
                    iva = cursor.getDouble(9),
                    total = cursor.getDouble(10)
                )
            )
        }
        return lista
    }

    fun buscarPorId(id: Int): Pedido? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Pedido WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            Pedido(
                id = cursor.getInt(0),
                clienteId = cursor.getString(1),
                restauranteId = cursor.getInt(2),
                repartidorId = cursor.getString(3),
                estado = cursor.getString(4),
                horaPedido = cursor.getString(5),
                horaEntrega = cursor.getString(6),
                subtotal = cursor.getDouble(7),
                costoTransporte = cursor.getDouble(8),
                iva = cursor.getDouble(9),
                total = cursor.getDouble(10)
            )
        } else null
    }

    fun actualizarEstado(id: Int, nuevoEstado: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        return db.update("Pedido", values, "id = ?", arrayOf(id.toString())) > 0
    }

    fun obtenerClientesConMasPedidosEntregados(): List<Pair<String, Int>> {
        val db = dbHelper.readableDatabase
        val resultado = mutableListOf<Pair<String, Int>>()

        val query = """
        SELECT cliente_id, COUNT(*) as total_entregados
        FROM Pedido
        WHERE estado = 'entregado'
        GROUP BY cliente_id
        HAVING total_entregados = (
            SELECT MAX(cantidad)
            FROM (
                SELECT COUNT(*) AS cantidad
                FROM Pedido
                WHERE estado = 'entregado'
                GROUP BY cliente_id
            )
        );
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val cedula = cursor.getString(0)
            val total = cursor.getInt(1)
            resultado.add(Pair(cedula, total))
        }
        cursor.close()
        return resultado
    }

}
