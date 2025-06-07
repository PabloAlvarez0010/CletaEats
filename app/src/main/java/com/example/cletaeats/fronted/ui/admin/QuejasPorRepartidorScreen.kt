package com.example.cletaeats.fronted.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import com.example.cletaeats.backend.dao.QuejaDAO
import com.example.cletaeats.backend.dao.RepartidorDAO
import com.example.cletaeats.backend.model.Repartidor
import com.example.cletaeats.backend.model.Queja

@Composable
fun QuejasPorRepartidorScreen(navController: NavController) {
    val context = LocalContext.current
    val repartidorDAO = remember { RepartidorDAO(context) }
    val quejaDAO = remember { QuejaDAO(context) }

    var repartidores by remember { mutableStateOf(emptyList<Repartidor>()) }
    var quejasMap by remember { mutableStateOf<Map<String, List<Queja>>>(emptyMap()) }
    var repartidorExpandido by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val lista = repartidorDAO.obtenerTodos()
        val mapa = lista.associate { it.cedula to quejaDAO.obtenerPorRepartidor(it.cedula) }
        repartidores = lista
        quejasMap = mapa
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quejas por Repartidor", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = Color(0xFFF5F5F5)
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(repartidores) { r ->
                Card(
                    backgroundColor = Color(0xFF06C167),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            repartidorExpandido = if (repartidorExpandido == r.cedula) null else r.cedula
                        },
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${r.nombre}", fontSize = 18.sp, color = Color.White)
                        Text("Cédula: ${r.cedula}", fontSize = 14.sp, color = Color.White)

                        AnimatedVisibility(visible = repartidorExpandido == r.cedula) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                val quejas = quejasMap[r.cedula] ?: emptyList()
                                if (quejas.isEmpty()) {
                                    Text("Sin quejas registradas.", color = Color.White)
                                } else {
                                    quejas.forEach {
                                        Divider(color = Color.White.copy(alpha = 0.4f), thickness = 1.dp)
                                        Text("• ${it.descripcion} (${it.fecha})", fontSize = 14.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}