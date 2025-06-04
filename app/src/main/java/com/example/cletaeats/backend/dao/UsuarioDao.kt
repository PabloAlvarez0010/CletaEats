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
                rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
            )
        } else null
    }

    fun insertar(usuario: Usuario): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("cedula", usuario.cedula)
            put("clave", usuario.clave)
            put("rol", usuario.rol)
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
                    rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
                )
            )
        }
        return lista
    }
}
