package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import com.example.cletaeats.backend.util.DatabaseHelper
import com.example.cletaeats.backend.model.Repartidor

class RepartidorDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertar(r: Repartidor): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("cedula", r.cedula)
            put("nombre", r.nombre)
            put("correo", r.correo)
            put("direccion", r.direccion)
            put("telefono", r.telefono)
            put("tarjeta", r.tarjeta)
            put("estado", r.estado)
            put("distancia_pedido", r.distanciaPedido)
            put("km_diarios", r.kmDiarios)
            put("costo_km_habil", r.costoKmHabil)
            put("costo_km_feriado", r.costoKmFeriado)
            put("amonestaciones", r.amonestaciones)
        }
        return db.insert("Repartidor", null, values) > 0
    }

    fun obtenerTodos(): List<Repartidor> {
        val lista = mutableListOf<Repartidor>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Repartidor", null)
        while (cursor.moveToNext()) {
            lista.add(
                Repartidor(
                    cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    tarjeta = cursor.getString(cursor.getColumnIndexOrThrow("tarjeta")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    distanciaPedido = cursor.getDouble(cursor.getColumnIndexOrThrow("distancia_pedido")),
                    kmDiarios = cursor.getDouble(cursor.getColumnIndexOrThrow("km_diarios")),
                    costoKmHabil = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_km_habil")),
                    costoKmFeriado = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_km_feriado")),
                    amonestaciones = cursor.getInt(cursor.getColumnIndexOrThrow("amonestaciones"))
                )
            )
        }
        return lista
    }

    fun buscarPorCedula(cedula: String): Repartidor? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Repartidor WHERE cedula = ?", arrayOf(cedula))
        return if (cursor.moveToFirst()) {
            Repartidor(
                cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                tarjeta = cursor.getString(cursor.getColumnIndexOrThrow("tarjeta")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                distanciaPedido = cursor.getDouble(cursor.getColumnIndexOrThrow("distancia_pedido")),
                kmDiarios = cursor.getDouble(cursor.getColumnIndexOrThrow("km_diarios")),
                costoKmHabil = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_km_habil")),
                costoKmFeriado = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_km_feriado")),
                amonestaciones = cursor.getInt(cursor.getColumnIndexOrThrow("amonestaciones"))
            )
        } else null
    }

    fun actualizar(r: Repartidor): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", r.nombre)
            put("correo", r.correo)
            put("direccion", r.direccion)
            put("telefono", r.telefono)
            put("tarjeta", r.tarjeta)
            put("estado", r.estado)
            put("distancia_pedido", r.distanciaPedido)
            put("km_diarios", r.kmDiarios)
            put("costo_km_habil", r.costoKmHabil)
            put("costo_km_feriado", r.costoKmFeriado)
            put("amonestaciones", r.amonestaciones)
        }
        return db.update("Repartidor", values, "cedula = ?", arrayOf(r.cedula)) > 0
    }

    fun eliminar(cedula: String): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete("Repartidor", "cedula = ?", arrayOf(cedula)) > 0
    }

    fun obtenerRepartidoresSinAmonestaciones(): List<Repartidor> {
        val lista = mutableListOf<Repartidor>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Repartidor WHERE amonestaciones = 0", null)
        while (cursor.moveToNext()) {
            lista.add(
                Repartidor(
                    cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    tarjeta = cursor.getString(cursor.getColumnIndexOrThrow("tarjeta")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    distanciaPedido = cursor.getDouble(cursor.getColumnIndexOrThrow("distancia_pedido")),
                    kmDiarios = cursor.getDouble(cursor.getColumnIndexOrThrow("km_diarios")),
                    costoKmHabil = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_km_habil")),
                    costoKmFeriado = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_km_feriado")),
                    amonestaciones = cursor.getInt(cursor.getColumnIndexOrThrow("amonestaciones"))
                )
            )
        }
        return lista
    }
}
