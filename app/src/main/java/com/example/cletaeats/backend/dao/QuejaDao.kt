package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.util.DatabaseHelper
import com.example.cletaeats.backend.model.Queja

class QuejaDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(q: Queja): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("repartidor_id", q.repartidorId)
            put("cliente_id", q.clienteId)
            put("descripcion", q.descripcion)
            put("fecha", q.fecha)
            put("calificacion", q.calificacion)
        }
        return db.insert("Queja", null, values) > 0
    }

    fun obtenerTodas(): List<Queja> {
        val lista = mutableListOf<Queja>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Queja", null)
        while (cursor.moveToNext()) {
            lista.add(
                Queja(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    repartidorId = cursor.getString(cursor.getColumnIndexOrThrow("repartidor_id")),
                    clienteId = cursor.getString(cursor.getColumnIndexOrThrow("cliente_id")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    calificacion = cursor.getInt(cursor.getColumnIndexOrThrow("calificacion"))
                )
            )
        }
        return lista
    }

    fun obtenerPorRepartidor(repartidorId: String): List<Queja> {
        val lista = mutableListOf<Queja>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Queja WHERE repartidor_id = ?", arrayOf(repartidorId))
        while (cursor.moveToNext()) {
            lista.add(
                Queja(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    repartidorId = cursor.getString(cursor.getColumnIndexOrThrow("repartidor_id")),
                    clienteId = cursor.getString(cursor.getColumnIndexOrThrow("cliente_id")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    calificacion = cursor.getInt(cursor.getColumnIndexOrThrow("calificacion"))
                )
            )
        }
        return lista
    }
}
