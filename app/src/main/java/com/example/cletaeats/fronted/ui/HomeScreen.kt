package com.example.cletaeats.fronted.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(rol: String, cedula: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido $rol") },
                elevation = 8.dp
            )
        }
    ) {
        Text(
            text = "Inicio del usuario con cédula: $cedula\n(Rol: $rol)",
            modifier = Modifier.padding(it).padding(24.dp)
        )
    }
}