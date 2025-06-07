package com.example.cletaeats.fronted.ui.admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.RepartidorDAO
import com.example.cletaeats.backend.model.Repartidor

private val UberGreen = Color(0xFF06C167)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun RepartidoresSinAmonestacionesScreen(navController: NavController) {
    val context = LocalContext.current
    val repartidorDAO = remember { RepartidorDAO(context) }
    var repartidores by remember { mutableStateOf(emptyList<Repartidor>()) }

    LaunchedEffect(Unit) {
        repartidores = repartidorDAO.obtenerRepartidoresSinAmonestaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repartidores sin Amonestaciones", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = UberGray
    ) { padding ->
        if (repartidores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay repartidores sin amonestaciones.")
            }
        } else {
            LazyColumn(modifier = Modifier
                .padding(padding)
                .padding(16.dp)
            ) {
                items(repartidores) { repartidor ->
                    Card(
                        backgroundColor = UberGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${repartidor.nombre}", fontSize = 18.sp, color = Color.White)
                            Text("Cédula: ${repartidor.cedula}", fontSize = 14.sp, color = Color.White)
                            Text("Correo: ${repartidor.correo}", fontSize = 14.sp, color = Color.White)
                            Text("Teléfono: ${repartidor.telefono}", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}