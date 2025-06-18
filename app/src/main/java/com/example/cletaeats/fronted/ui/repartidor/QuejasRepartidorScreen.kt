package com.example.cletaeats.fronted.ui.repartidor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.*

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun QuejasRepartidorScreen(navController: NavController, cedulaRepartidor: String) {
    val context = LocalContext.current
    val quejaDAO = remember { QuejaDAO(context) }
    val clienteDAO = remember { ClienteDAO(context) }
    val quejas = remember { quejaDAO.obtenerPorRepartidor(cedulaRepartidor) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Quejas", color = Color.White) },
                backgroundColor = Color(0xFF000000),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (quejas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes quejas registradas.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
         else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(quejas) { queja ->
                    val cliente = clienteDAO.buscarPorCedula(queja.clienteId)
                    Card(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, shape = MaterialTheme.shapes.medium),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Cliente: ${cliente?.nombre ?: "Desconocido"}", fontWeight = FontWeight.Bold)
                            Text("Fecha: ${queja.fecha}")
                            Text("Comentario: ${queja.descripcion}")
                            Text("Calificación: ${queja.calificacion} / 5")
                        }
                    }
                }
            }
        }
    }
}
