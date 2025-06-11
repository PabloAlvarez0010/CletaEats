package com.example.cletaeats.backend.dao

import android.content.ContentValues
import android.content.Context
import androidx.core.database.getStringOrNull
import com.example.cletaeats.backend.model.ClientePedidos
import com.example.cletaeats.backend.model.Combo
import com.example.cletaeats.backend.model.ComboDetalle
import com.example.cletaeats.backend.model.Pedido
import com.example.cletaeats.backend.model.PedidoDetalle
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
    fun obtenerRestaurantesConMasPedidosEntregados(): List<Pair<Int, Int>> {
        val db = dbHelper.readableDatabase
        val resultado = mutableListOf<Pair<Int, Int>>()

        val subquery = """
        SELECT restaurante_id, COUNT(*) as cantidad
        FROM Pedido
        WHERE estado = 'entregado'
        GROUP BY restaurante_id
    """

        val mainQuery = """
        SELECT restaurante_id, cantidad FROM (
            $subquery
        ) WHERE cantidad = (
            SELECT MAX(cantidad) FROM (
                $subquery
            )
        );
    """.trimIndent()

        val cursor = db.rawQuery(mainQuery, null)
        while (cursor.moveToNext()) {
            resultado.add(Pair(cursor.getInt(0), cursor.getInt(1)))
        }
        cursor.close()
        return resultado
    }


    fun obtenerRestaurantesConMenosPedidosEntregados(): List<Pair<Int, Int>> {
        val db = dbHelper.readableDatabase
        val resultado = mutableListOf<Pair<Int, Int>>()

        val subquery = """
        SELECT restaurante_id, COUNT(*) as cantidad
        FROM Pedido
        WHERE estado = 'entregado'
        GROUP BY restaurante_id
    """

        val mainQuery = """
        SELECT restaurante_id, cantidad FROM (
            $subquery
        ) WHERE cantidad = (
            SELECT MIN(cantidad) FROM (
                $subquery
            )
        );
    """.trimIndent()

        val cursor = db.rawQuery(mainQuery, null)
        while (cursor.moveToNext()) {
            resultado.add(Pair(cursor.getInt(0), cursor.getInt(1)))
        }
        cursor.close()
        return resultado
    }

    fun obtenerMontosTotalesPorRestaurante(): List<Pair<Int, Double>> {
        val db = dbHelper.readableDatabase
        val resultado = mutableListOf<Pair<Int, Double>>()

        val query = """
        SELECT restaurante_id, SUM(subtotal) as total
        FROM Pedido
        WHERE estado = 'entregado'
        GROUP BY restaurante_id;
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val restauranteId = cursor.getInt(0)
            val montoTotal = cursor.getDouble(1)
            resultado.add(Pair(restauranteId, montoTotal))
        }
        cursor.close()
        return resultado
    }

    fun obtenerMontoTotalGeneral(): Double {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT SUM(subtotal)
        FROM Pedido
        WHERE estado = 'entregado';
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        val total = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return total
    }

    fun crearPedidoDesdeCarrito(
        cedulaCliente: String,
        carrito: List<Pair<Combo, Int>>,
        restauranteId: Int,
        subtotal: Double,
        transporte: Double,
        iva: Double,
        total: Double
    ): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            // Obtener repartidor disponible con menos de 4 amonestaciones
            val repartidorId = run {
                val cursor = db.rawQuery(
                    "SELECT cedula FROM Repartidor WHERE estado = 'disponible' AND amonestaciones < 4 LIMIT 1",
                    null
                )
                val result = if (cursor.moveToFirst()) cursor.getString(0) else null
                cursor.close()
                result
            }

            // Registrar hora actual como hora del pedido
            val horaActual = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())

            // Insertar en tabla Pedido
            val pedidoValues = ContentValues().apply {
                put("cliente_id", cedulaCliente)
                put("restaurante_id", restauranteId)
                put("repartidor_id", repartidorId)
                put("estado", "en preparación")
                put("hora_pedido", horaActual)
                put("hora_entrega", null as String?)
                put("subtotal", subtotal)
                put("costo_transporte", transporte)
                put("iva", iva)
                put("total", total)
            }

            val pedidoId = db.insert("Pedido", null, pedidoValues)
            if (pedidoId == -1L) return false

            // Insertar combos en PedidoCombo
            for ((combo, cantidad) in carrito) {
                val detalleValues = ContentValues().apply {
                    put("pedido_id", pedidoId)
                    put("combo_id", combo.id)
                    put("cantidad", cantidad)
                }
                db.insert("PedidoCombo", null, detalleValues)
            }

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerHistorialPedidosPorCliente(cedulaCliente: String): List<PedidoDetalle> {
        val db = dbHelper.readableDatabase
        val pedidos = mutableListOf<PedidoDetalle>()

        val pedidoCursor = db.rawQuery("""
        SELECT p.id, p.estado, p.hora_pedido, p.hora_entrega, p.total,
               r.nombre AS nombreRestaurante, r.tipo_comida AS tipoComida
        FROM Pedido p
        JOIN Restaurante r ON p.restaurante_id = r.id
        WHERE p.cliente_id = ?
        ORDER BY p.hora_pedido DESC
    """.trimIndent(), arrayOf(cedulaCliente))

        while (pedidoCursor.moveToNext()) {
            val pedidoId = pedidoCursor.getInt(0)
            val combos = mutableListOf<ComboDetalle>()

            val comboCursor = db.rawQuery("""
            SELECT c.numero, c.descripcion, c.precio, pc.cantidad
            FROM PedidoCombo pc
            JOIN Combo c ON pc.combo_id = c.id
            WHERE pc.pedido_id = ?
        """.trimIndent(), arrayOf(pedidoId.toString()))

            while (comboCursor.moveToNext()) {
                combos.add(
                    ComboDetalle(
                        numero = comboCursor.getInt(0),
                        descripcion = comboCursor.getString(1),
                        precio = comboCursor.getDouble(2),
                        cantidad = comboCursor.getInt(3)
                    )
                )
            }
            comboCursor.close()

            pedidos.add(
                PedidoDetalle(
                    id = pedidoId,
                    estado = pedidoCursor.getString(1),
                    horaPedido = pedidoCursor.getString(2),
                    horaEntrega = pedidoCursor.getString(3),
                    total = pedidoCursor.getDouble(4),
                    restaurante = pedidoCursor.getString(5),
                    tipoComida = pedidoCursor.getString(6),
                    combos = combos
                )
            )
        }
        pedidoCursor.close()
        return pedidos
    }
    fun obtenerHistorialEntregadoPorCliente(cedulaCliente: String): List<PedidoDetalle> {
        val db = dbHelper.readableDatabase
        val pedidos = mutableListOf<PedidoDetalle>()

        val pedidoCursor = db.rawQuery("""
        SELECT p.id, p.estado, p.hora_pedido, p.hora_entrega, p.total,
               r.nombre AS nombreRestaurante, r.tipo_comida AS tipoComida,
               rep.nombre AS nombreRepartidor, rep.cedula AS cedulaRepartidor
        FROM Pedido p
        JOIN Restaurante r ON p.restaurante_id = r.id
        LEFT JOIN Repartidor rep ON p.repartidor_id = rep.cedula
        WHERE p.cliente_id = ? AND p.estado = 'entregado'
        ORDER BY p.hora_pedido DESC
    """.trimIndent(), arrayOf(cedulaCliente))

        while (pedidoCursor.moveToNext()) {
            val pedidoId = pedidoCursor.getInt(0)
            val combos = mutableListOf<ComboDetalle>()

            val comboCursor = db.rawQuery("""
            SELECT c.numero, c.descripcion, c.precio, pc.cantidad
            FROM PedidoCombo pc
            JOIN Combo c ON pc.combo_id = c.id
            WHERE pc.pedido_id = ?
        """.trimIndent(), arrayOf(pedidoId.toString()))

            while (comboCursor.moveToNext()) {
                combos.add(
                    ComboDetalle(
                        numero = comboCursor.getInt(0),
                        descripcion = comboCursor.getString(1),
                        precio = comboCursor.getDouble(2),
                        cantidad = comboCursor.getInt(3)
                    )
                )
            }
            comboCursor.close()

            pedidos.add(
                PedidoDetalle(
                    id = pedidoId,
                    estado = pedidoCursor.getString(1),
                    horaPedido = pedidoCursor.getString(2),
                    horaEntrega = pedidoCursor.getString(3),
                    total = pedidoCursor.getDouble(4),
                    restaurante = pedidoCursor.getString(5),
                    tipoComida = pedidoCursor.getString(6),
                    combos = combos,
                    repartidorNombre = pedidoCursor.getStringOrNull(7),
                    repartidorCedula = pedidoCursor.getStringOrNull(8)
                )
            )
        }
        pedidoCursor.close()
        return pedidos
    }


    fun marcarPedidoEnCamino(pedidoId: Int, repartidorId: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("estado", "en camino")
            put("repartidor_id", repartidorId)
        }
        return db.update("Pedido", values, "id = ?", arrayOf(pedidoId.toString())) > 0
    }
    fun marcarPedidoEntregado(pedidoId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("estado", "entregado")
            put("hora_entrega", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date()))
        }
        return db.update("Pedido", values, "id = ?", arrayOf(pedidoId.toString())) > 0
    }

    fun obtenerPedidosPendientesPorRestaurante(restauranteId: Int): List<Pedido> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Pedido>()
        val cursor = db.rawQuery(
            """
        SELECT * FROM Pedido
        WHERE restaurante_id = ? AND estado = 'en preparación'
        """.trimIndent(),
            arrayOf(restauranteId.toString())
        )
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
        cursor.close()
        return lista
    }
    fun obtenerPorRestauranteYEstado(restauranteId: Int, estado: String): List<Pedido> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Pedido>()
        val cursor = db.rawQuery(
            """
        SELECT * FROM Pedido
        WHERE restaurante_id = ? AND estado = ?
        """.trimIndent(),
            arrayOf(restauranteId.toString(), estado)
        )
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
        cursor.close()
        return lista
    }
    fun obtenerPorRepartidorYEstado(repartidorId: String, estado: String): List<Pedido> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Pedido>()
        val cursor = db.rawQuery(
            """
        SELECT * FROM Pedido
        WHERE repartidor_id = ? AND estado = ?
        """.trimIndent(),
            arrayOf(repartidorId, estado)
        )
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
        cursor.close()
        return lista
    }

    fun obtenerPedidosEntregadosPorRepartidor(cedula: String): List<Pedido> {
        val pedidos = mutableListOf<Pedido>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Pedido WHERE repartidor_id = ? AND estado = 'entregado'",
            arrayOf(cedula)
        )

        while (cursor.moveToNext()) {
            pedidos.add(
                Pedido(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    clienteId = cursor.getString(cursor.getColumnIndexOrThrow("cliente_id")),
                    restauranteId = cursor.getInt(cursor.getColumnIndexOrThrow("restaurante_id")),
                    repartidorId = cursor.getString(cursor.getColumnIndexOrThrow("repartidor_id")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    horaPedido = cursor.getString(cursor.getColumnIndexOrThrow("hora_pedido")),
                    horaEntrega = cursor.getString(cursor.getColumnIndexOrThrow("hora_entrega")),
                    subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")),
                    costoTransporte = cursor.getDouble(cursor.getColumnIndexOrThrow("costo_transporte")),
                    iva = cursor.getDouble(cursor.getColumnIndexOrThrow("iva")),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                )
            )
        }

        cursor.close()
        return pedidos
    }


    fun obtenerCombosPorPedido(pedidoId: Int): List<Pair<Combo, Int>> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Pair<Combo, Int>>()

        val query = """
        SELECT c.id, c.numero, c.precio, c.descripcion, c.restaurante_id, pc.cantidad
        FROM PedidoCombo pc
        INNER JOIN Combo c ON c.id = pc.combo_id
        WHERE pc.pedido_id = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(pedidoId.toString()))
        while (cursor.moveToNext()) {
            val combo = Combo(
                id = cursor.getInt(0),
                numero = cursor.getInt(1),
                precio = cursor.getDouble(2),
                descripcion = cursor.getString(3),
                restauranteId = cursor.getInt(4)
            )
            val cantidad = cursor.getInt(5)
            lista.add(combo to cantidad)
        }
        cursor.close()
        return lista
    }
    fun obtenerClientesConMasPedidos(): List<ClientePedidos> {
        val db = dbHelper.readableDatabase
        val resultado = mutableListOf<ClientePedidos>()

        // Paso 1: contar pedidos por cliente
        val conteoCursor = db.rawQuery("""
        SELECT c.cedula, c.nombre, COUNT(p.id) as cantidad
        FROM Pedido p
        JOIN Cliente c ON p.cliente_id = c.cedula
        WHERE p.estado = 'entregado'
        GROUP BY c.cedula
        ORDER BY cantidad DESC
    """.trimIndent(), null)

        if (!conteoCursor.moveToFirst()) return emptyList()

        val maxCantidad = conteoCursor.getInt(conteoCursor.getColumnIndexOrThrow("cantidad"))

        // Paso 2: filtrar todos los clientes que tienen esa misma cantidad
        do {
            val cantidad = conteoCursor.getInt(conteoCursor.getColumnIndexOrThrow("cantidad"))
            if (cantidad < maxCantidad) break

            val cedulaCliente = conteoCursor.getString(conteoCursor.getColumnIndexOrThrow("cedula"))
            val nombreCliente = conteoCursor.getString(conteoCursor.getColumnIndexOrThrow("nombre"))

            // Paso 3: obtener restaurantes únicos para ese cliente
            val restaurantesCursor = db.rawQuery("""
            SELECT DISTINCT r.nombre
            FROM Pedido p
            JOIN Restaurante r ON p.restaurante_id = r.id
            WHERE p.cliente_id = ? AND p.estado = 'entregado'
        """.trimIndent(), arrayOf(cedulaCliente))

            val restaurantes = mutableListOf<String>()
            while (restaurantesCursor.moveToNext()) {
                restaurantes.add(restaurantesCursor.getString(0))
            }
            restaurantesCursor.close()

            resultado.add(
                ClientePedidos(
                    clienteNombre = nombreCliente,
                    cantidadPedidos = cantidad,
                    restaurantes = restaurantes
                )
            )
        } while (conteoCursor.moveToNext())

        conteoCursor.close()
        return resultado
    }

    fun obtenerHoraPico(): Pair<String, Int>? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("""
        SELECT strftime('%H', hora_pedido) AS hora, COUNT(*) as total
        FROM Pedido
        WHERE estado = 'entregado'
        GROUP BY hora
        ORDER BY total DESC
        LIMIT 1
    """.trimIndent(), null)

        var resultado: Pair<String, Int>? = null
        if (cursor.moveToFirst()) {
            val hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"))
            val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            resultado = Pair(hora, total)
        }
        cursor.close()
        return resultado
    }
    fun obtenerPedidosPorHora(): Map<Int, Int> {
        val db = dbHelper.readableDatabase
        val resultado = mutableMapOf<Int, Int>()
        val cursor = db.rawQuery("""
        SELECT strftime('%H', hora_pedido) AS hora, COUNT(*) 
        FROM Pedido
        GROUP BY hora
        ORDER BY hora
    """.trimIndent(), null)

        while (cursor.moveToNext()) {
            val hora = cursor.getString(0)?.toIntOrNull() ?: continue
            val cantidad = cursor.getInt(1)
            resultado[hora] = cantidad
        }
        cursor.close()
        return resultado
    }



}
