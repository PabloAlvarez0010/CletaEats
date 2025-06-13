package com.example.cletaeats.fronted.ui.admin

import android.widget.Toast
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

    fun cargarClientes() {
        clientes = if (mostrarActivos) {
            clienteDAO.obtenerClientesActivos()
        } else {
            clienteDAO.obtenerClientesInactivos()
        }
    }

    LaunchedEffect(mostrarActivos) {
        cargarClientes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Clientes", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
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
                    onCheckedChange = {
                        mostrarActivos = it
                        cargarClientes()
                    },
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

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val nuevoEstado = if (cliente.estado == "activo") "suspendido" else "activo"
                                        val clienteActualizado = cliente.copy(estado = nuevoEstado)
                                        val exito = clienteDAO.actualizar(clienteActualizado)
                                        if (exito) {
                                            Toast.makeText(
                                                context,
                                                "Cliente ${if (mostrarActivos) "suspendido" else "activado"} correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            cargarClientes()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error al actualizar el estado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (mostrarActivos) Color.Red else Color(0xFF06C167)
                                    )
                                ) {
                                    Text(
                                        if (mostrarActivos) "Suspender" else "Activar",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

