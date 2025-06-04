package com.example.cletaeats.fronted.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberWhite = Color(0xFFFFFFFF)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun HomeScreen(rol: String, cedula: String) {
    val titulo = when (rol.lowercase()) {
        "cliente" -> "Inicio del Cliente"
        "repartidor" -> "Panel del Repartidor"
        "admin" -> "Dashboard de Administración"
        else -> "Inicio"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo, color = UberWhite) },
                backgroundColor = UberBlack,
                elevation = 6.dp
            )
        },
        backgroundColor = UberGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "¡Hola, $rol!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = UberBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu cédula es: $cedula",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Próximamente verás aquí las funcionalidades disponibles para tu rol.",
                fontSize = 16.sp,
                color = UberBlack
            )
        }
    }
}
