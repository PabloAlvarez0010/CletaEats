package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.cletaeats.backend.util.DatabaseHelper
import com.example.cletaeats.backend.model.Cliente

class ClienteDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(cliente: Cliente): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("cedula", cliente.cedula)
            put("nombre", cliente.nombre)
            put("direccion", cliente.direccion)
            put("tarjeta", cliente.tarjeta)
            put("telefono", cliente.telefono)
            put("correo", cliente.correo)
            put("estado", cliente.estado)
        }
        return db.insert("Cliente", null, values) > 0
    }

    fun obtenerTodos(): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Cliente", null)
        while (cursor.moveToNext()) {
            lista.add(
                Cliente(
                    cedula = cursor.getString(0),
                    nombre = cursor.getString(1),
                    direccion = cursor.getString(2),
                    tarjeta = cursor.getString(3),
                    telefono = cursor.getString(4),
                    correo = cursor.getString(5),
                    estado = cursor.getString(6)
                )
            )
        }
        return lista
    }
    fun obtenerClientesActivos(): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Cliente WHERE estado = 'activo'", null)
        while (cursor.moveToNext()) {
            lista.add(
                Cliente(
                    cedula = cursor.getString(0),
                    nombre = cursor.getString(1),
                    direccion = cursor.getString(2),
                    tarjeta = cursor.getString(3),
                    telefono = cursor.getString(4),
                    correo = cursor.getString(5),
                    estado = cursor.getString(6)
                )
            )
        }
        return lista
    }
    fun obtenerClientesInactivos(): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Cliente WHERE estado = 'suspendido'", null)
        while (cursor.moveToNext()) {
            lista.add(
                Cliente(
                    cedula = cursor.getString(0),
                    nombre = cursor.getString(1),
                    direccion = cursor.getString(2),
                    tarjeta = cursor.getString(3),
                    telefono = cursor.getString(4),
                    correo = cursor.getString(5),
                    estado = cursor.getString(6)
                )
            )
        }
        return lista
    }

    fun buscarPorCedula(cedula: String): Cliente? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Cliente WHERE cedula = ?", arrayOf(cedula))
        return if (cursor.moveToFirst()) {
            Cliente(
                cedula = cursor.getString(0),
                nombre = cursor.getString(1),
                direccion = cursor.getString(2),
                tarjeta = cursor.getString(3),
                telefono = cursor.getString(4),
                correo = cursor.getString(5),
                estado = cursor.getString(6)
            )
        } else null
    }

    fun actualizar(cliente: Cliente): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", cliente.nombre)
            put("direccion", cliente.direccion)
            put("tarjeta", cliente.tarjeta)
            put("telefono", cliente.telefono)
            put("correo", cliente.correo)
            put("estado", cliente.estado)
        }
        return db.update("Cliente", values, "cedula = ?", arrayOf(cliente.cedula)) > 0
    }

    fun eliminar(cedula: String): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete("Cliente", "cedula = ?", arrayOf(cedula)) > 0
    }

    fun clienteActivo(cedula: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT estado FROM Cliente WHERE cedula = ?",
            arrayOf(cedula)
        )
        var activo = false
        if (cursor.moveToFirst()) {
            activo = cursor.getString(0) == "activo"
        }
        cursor.close()
        return activo
    }

}


