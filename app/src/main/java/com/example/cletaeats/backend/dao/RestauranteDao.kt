package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.util.DatabaseHelper
import com.example.cletaeats.backend.model.Restaurante

class RestauranteDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(r: Restaurante): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", r.nombre)
            put("cedula_juridica", r.cedulaJuridica)
            put("direccion", r.direccion)
            put("tipo_comida", r.tipoComida)
        }
        return db.insert("Restaurante", null, values) > 0
    }

    fun obtenerTodos(): List<Restaurante> {
        val lista = mutableListOf<Restaurante>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Restaurante", null)
        while (cursor.moveToNext()) {
            lista.add(
                Restaurante(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cedulaJuridica = cursor.getString(cursor.getColumnIndexOrThrow("cedula_juridica")),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    tipoComida = cursor.getString(cursor.getColumnIndexOrThrow("tipo_comida"))
                )
            )
        }
        return lista
    }

    fun buscarPorCedulaJuridica(cedulaJuridica: String): Restaurante? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Restaurante WHERE cedula_juridica = ?", arrayOf(cedulaJuridica))
        return if (cursor.moveToFirst()) {
            Restaurante(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cedulaJuridica = cursor.getString(cursor.getColumnIndexOrThrow("cedula_juridica")),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                tipoComida = cursor.getString(cursor.getColumnIndexOrThrow("tipo_comida"))
            )
        } else null
    }

    fun actualizar(r: Restaurante): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", r.nombre)
            put("direccion", r.direccion)
            put("tipo_comida", r.tipoComida)
        }
        return db.update("Restaurante", values, "cedula_juridica = ?", arrayOf(r.cedulaJuridica)) > 0
    }

    fun eliminarPorCedulaJuridica(cedulaJuridica: String): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete("Restaurante", "cedula_juridica = ?", arrayOf(cedulaJuridica)) > 0
    }

    fun buscarPorId(id: Int): Restaurante? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Restaurante WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            Restaurante(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cedulaJuridica = cursor.getString(cursor.getColumnIndexOrThrow("cedula_juridica")),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                tipoComida = cursor.getString(cursor.getColumnIndexOrThrow("tipo_comida"))
            )
        } else null
    }
}
