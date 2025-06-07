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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.model.Cliente

private val UberGreen = Color(0xFF06C167)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun ListaClientesScreen(navController: NavController) {
    val context = LocalContext.current
    val clienteDAO = remember { ClienteDAO(context) }

    var mostrarActivos by remember { mutableStateOf(true) }
    var clientes by remember { mutableStateOf(emptyList<Cliente>()) }

    // Cargar los datos según el estado del switch
    LaunchedEffect(mostrarActivos) {
        clientes = if (mostrarActivos) {
            clienteDAO.obtenerClientesActivos()
        } else {
            clienteDAO.obtenerClientesInactivos()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Clientes", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // Regresa a AdminHome
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = UberGray
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Switch visual moderno
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = if (mostrarActivos) "Mostrando: Activos" else "Mostrando: Suspendidos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = mostrarActivos,
                    onCheckedChange = { mostrarActivos = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = UberGreen,
                        uncheckedThumbColor = Color.Gray
                    )
                )
            }

            if (clientes.isEmpty()) {
                Text("No hay clientes en esta categoría.")
            } else {
                LazyColumn {
                    items(clientes) { cliente ->
                        Card(
                            backgroundColor = UberGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = 6.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Nombre: ${cliente.nombre}", color = Color.White, fontSize = 18.sp)
                                Text("Cédula: ${cliente.cedula}", color = Color.White, fontSize = 14.sp)
                                Text("Correo: ${cliente.correo}", color = Color.White, fontSize = 14.sp)
                                Text("Estado: ${cliente.estado}", color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
