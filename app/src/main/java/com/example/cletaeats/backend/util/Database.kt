package com.example.cletaeats.backend.util

import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val DB_URL = "jdbc:sqlite:C:/Users/jpabl/OneDrive/Desktop/Moviles/Proyecto_Moviles_CletaEats.db"
    val connection: Connection by lazy {
        DriverManager.getConnection(DB_URL)
    }
}
