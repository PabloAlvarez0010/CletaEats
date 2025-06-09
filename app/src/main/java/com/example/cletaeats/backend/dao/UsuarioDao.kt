package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.util.DatabaseHelper
import com.example.cletaeats.backend.model.Usuario

class UsuarioDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun autenticar(cedula: String, clave: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Usuario WHERE cedula = ? AND clave = ?",
            arrayOf(cedula, clave)
        )
        return if (cursor.moveToFirst()) {
            Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
                verificado = cursor.getString(cursor.getColumnIndexOrThrow("verificado"))
            )
        } else null
    }

    fun insertar(cedula: String, clave: String, rol: String, verificado: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("cedula", cedula)
            put("clave", clave)
            put("rol", rol)
            put("verificado", verificado)
        }
        return db.insert("Usuario", null, values) > 0
    }

    fun obtenerTodos(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Usuario", null)
        val lista = mutableListOf<Usuario>()
        while (cursor.moveToNext()) {
            lista.add(
                Usuario(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                    clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                    rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
                    verificado = cursor.getString(cursor.getColumnIndexOrThrow("verificado"))
                )
            )
        }
        return lista
    }

    fun obtenerPorCedula(cedula: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Usuario WHERE cedula = ?", arrayOf(cedula))
        return if (cursor.moveToFirst()) {
            Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
                verificado = cursor.getString(cursor.getColumnIndexOrThrow("verificado"))
            )
        } else null
    }
    fun obtenerClientesNoVerificados(): List<String> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT cedula FROM Usuario WHERE rol = 'cliente' AND verificado = 'no'", null
        )
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        return lista
    }
    fun obtenerRepartidoresNoVerificados(): List<String> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT cedula FROM Usuario WHERE rol = 'repartidor' AND verificado = 'no'", null
        )
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        return lista
    }
    fun obtenerRestaurantesNoVerificados(): List<String> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT cedula FROM Usuario WHERE rol = 'restaurante' AND verificado = 'no'", null
        )
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        return lista
    }

    fun actualizarVerificado(cedula: String, estado: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("verificado", estado)
        }
        return db.update("Usuario", values, "cedula = ?", arrayOf(cedula)) > 0
    }
    fun eliminar(cedula: String): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete("Usuario", "cedula = ?", arrayOf(cedula)) > 0
    }


}
