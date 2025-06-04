package com.example.cletaeats.backend.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, "Proyecto_Moviles_CletaEats.db", null, 1
) {
    override fun onCreate(db: SQLiteDatabase) {
        // Crear tablas
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Cliente (
                cedula TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                direccion TEXT NOT NULL,
                tarjeta TEXT NOT NULL,
                telefono TEXT NOT NULL,
                correo TEXT NOT NULL,
                estado TEXT NOT NULL CHECK (estado IN ('activo', 'suspendido'))
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Repartidor (
                cedula TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL,
                direccion TEXT NOT NULL,
                telefono TEXT NOT NULL,
                tarjeta TEXT NOT NULL,
                estado TEXT NOT NULL CHECK (estado IN ('disponible', 'ocupado')),
                distancia_pedido REAL,
                km_diarios REAL NOT NULL DEFAULT 0,
                costo_km_habil REAL NOT NULL DEFAULT 1000,
                costo_km_feriado REAL NOT NULL DEFAULT 1500,
                amonestaciones INTEGER NOT NULL DEFAULT 0
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Restaurante (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                cedula_juridica TEXT UNIQUE NOT NULL,
                direccion TEXT NOT NULL,
                tipo_comida TEXT NOT NULL
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Combo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER NOT NULL CHECK (numero BETWEEN 1 AND 9),
                precio REAL NOT NULL,
                descripcion TEXT,
                restaurante_id INTEGER NOT NULL,
                FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id TEXT NOT NULL,
                restaurante_id INTEGER NOT NULL,
                repartidor_id TEXT,
                estado TEXT NOT NULL CHECK (estado IN ('en preparación', 'en camino', 'suspendido', 'entregado')),
                hora_pedido TEXT NOT NULL,
                hora_entrega TEXT,
                subtotal REAL NOT NULL,
                costo_transporte REAL NOT NULL,
                iva REAL NOT NULL,
                total REAL NOT NULL,
                FOREIGN KEY (cliente_id) REFERENCES Cliente(cedula),
                FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id),
                FOREIGN KEY (repartidor_id) REFERENCES Repartidor(cedula)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS PedidoCombo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pedido_id INTEGER NOT NULL,
                combo_id INTEGER NOT NULL,
                cantidad INTEGER NOT NULL CHECK (cantidad > 0),
                FOREIGN KEY (pedido_id) REFERENCES Pedido(id),
                FOREIGN KEY (combo_id) REFERENCES Combo(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Queja (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                repartidor_id TEXT NOT NULL,
                cliente_id TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY (repartidor_id) REFERENCES Repartidor(cedula),
                FOREIGN KEY (cliente_id) REFERENCES Cliente(cedula)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cedula TEXT NOT NULL UNIQUE,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL CHECK (rol IN ('cliente', 'repartidor', 'admin'))
            );
        """.trimIndent())

        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Implementa lógica de migración si cambias el esquema en el futuro
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        // Insertar Clientes
        db.execSQL("INSERT INTO Cliente VALUES ('111', 'Carlos Jiménez', 'Heredia Centro', '101', '88884444', 'carlos@gmail.com', 'activo');")
        db.execSQL("INSERT INTO Cliente VALUES ('222', 'María Fernández', 'Barva', '202', '88995566', 'mariaf@hotmail.com', 'activo');")
        db.execSQL("INSERT INTO Cliente VALUES ('333', 'Luis Ramírez', 'San Pablo', '303', '87001122', 'luis@correo.com', 'suspendido');")

        // Insertar Repartidores
        db.execSQL("INSERT INTO Repartidor VALUES ('444', 'Andrés Mora', 'andresm@correo.com', 'Heredia', '88001122', '404', 'disponible', 3.2, 10.5, 1000, 1500, 0);")
        db.execSQL("INSERT INTO Repartidor VALUES ('555', 'Sofía Zúñiga', 'sofiaz@correo.com', 'El Bosque', '87003344', '505', 'ocupado', 1.5, 6.7, 1000, 1500, 2);")
        db.execSQL("INSERT INTO Repartidor VALUES ('666', 'Esteban Vargas', 'estebv@correo.com', 'San Francisco', '89997766', '606', 'disponible', 0.0, 0.0, 1000, 1500, 4);")

        // Insertar Usuario
        db.execSQL("INSERT INTO Usuario (cedula, clave, rol) VALUES ('111', '1234', 'cliente');")
        db.execSQL("INSERT INTO Usuario (cedula, clave, rol) VALUES ('444', '1234', 'repartidor');")
        db.execSQL("INSERT INTO Usuario (cedula, clave, rol) VALUES ('777', '1234', 'admin');")

        db.execSQL("""
            INSERT INTO Restaurante (nombre, cedula_juridica, direccion, tipo_comida) VALUES
                ('Rápido Loco', '3101010101', 'Heredia centro, 50 m sur de la plaza', 'rápida'),
                ('China Feliz', '3202020202', 'San Rafael, frente a la farmacia', 'china'),
                ('Salud Vital', '3303030303', 'Barva, costado este del parque', 'saludable');
        """.trimIndent())

        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (1, 4000, 'Hamburguesa clásica con papas y refresco', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (2, 5000, 'Hamburguesa doble carne con papas', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (3, 6000, 'Tenders de pollo con aderezo y bebida', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (4, 7000, 'Hot dog gigante con nachos', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (5, 8000, 'Pizza personal y bebida', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (6, 9000, 'Burrito grande con papas y soda', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (7, 10000, 'Wrap mixto con ensalada', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (8, 11000, 'Combo de alitas BBQ con papas y soda', 1);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (9, 12000, 'Hamburguesa premium con papas y batido', 1);")

        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (1, 4000, 'Arroz cantonés pequeño con bebida', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (2, 5000, 'Chop suey mixto y gaseosa', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (3, 6000, 'Pollo agridulce con arroz blanco', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (4, 7000, 'Fideos con vegetales y bebida', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (5, 8000, 'Combo de wantán con arroz especial', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (6, 9000, 'Camarones empanizados y bebida', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (7, 10000, 'Cerdo en salsa negra con arroz', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (8, 11000, 'Combo familiar pequeño', 2);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (9, 12000, 'Pollo kung pao con arroz y gaseosa', 2);")

        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (1, 4000, 'Ensalada verde con jugo natural', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (2, 5000, 'Wrap integral con vegetales y bebida', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (3, 6000, 'Pollo a la plancha con arroz integral', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (4, 7000, 'Tazón de quinoa con vegetales', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (5, 8000, 'Hamburguesa vegana y agua de pipa', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (6, 9000, 'Filete de pescado al vapor con ensalada', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (7, 10000, 'Ensalada tropical y smoothie', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (8, 11000, 'Bowl de avena con frutas y yogurt', 3);")
        db.execSQL("INSERT INTO Combo (numero, precio, descripcion, restaurante_id) VALUES (9, 12000, 'Combo vegetariano premium con bebida', 3);")

    }
}