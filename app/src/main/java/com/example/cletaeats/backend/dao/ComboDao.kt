package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.model.Combo
import com.example.cletaeats.backend.util.DatabaseHelper
import java.sql.ResultSet

class ComboDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(c: Combo): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", c.numero)
            put("precio", c.precio)
            put("descripcion", c.descripcion)
            put("restaurante_id", c.restauranteId)
        }
        return db.insert("Combo", null, values) > 0
    }

    fun obtenerTodos(): List<Combo> {
        val lista = mutableListOf<Combo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Combo", null)
        while (cursor.moveToNext()) {
            lista.add(
                Combo(
                    id = cursor.getInt(0),
                    numero = cursor.getInt(1),
                    precio = cursor.getDouble(2),
                    descripcion = cursor.getString(3),
                    restauranteId = cursor.getInt(4)
                )
            )
        }
        return lista
    }

    fun obtenerPorRestaurante(restauranteId: Int): List<Combo> {
        val lista = mutableListOf<Combo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Combo WHERE restaurante_id = ?", arrayOf(restauranteId.toString()))
        while (cursor.moveToNext()) {
            lista.add(
                Combo(
                    id = cursor.getInt(0),
                    numero = cursor.getInt(1),
                    precio = cursor.getDouble(2),
                    descripcion = cursor.getString(3),
                    restauranteId = cursor.getInt(4)
                )
            )
        }
        return lista
    }

    fun buscarPorId(id: Int): Combo? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Combo WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            Combo(
                id = cursor.getInt(0),
                numero = cursor.getInt(1),
                precio = cursor.getDouble(2),
                descripcion = cursor.getString(3),
                restauranteId = cursor.getInt(4)
            )
        } else null
    }

    fun actualizar(c: Combo): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", c.numero)
            put("precio", c.precio)
            put("descripcion", c.descripcion)
            put("restaurante_id", c.restauranteId)
        }
        return db.update("Combo", values, "id = ?", arrayOf(c.id.toString())) > 0
    }

    fun eliminar(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete("Combo", "id = ?", arrayOf(id.toString())) > 0
    }
}
