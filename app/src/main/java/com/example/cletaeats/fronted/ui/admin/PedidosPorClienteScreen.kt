package com.example.cletaeats.fronted.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.model.Cliente
import com.example.cletaeats.backend.model.PedidoDetalle

@Composable
fun PedidosPorClienteScreen(navController: NavController) {
    val context = LocalContext.current
    val clienteDAO = remember { ClienteDAO(context) }
    val pedidoDAO = remember { PedidoDAO(context) }

    val clientes = remember { clienteDAO.obtenerTodos() }

    var clienteSeleccionado by remember { mutableStateOf<Cliente?>(null) }
    var pedidosSeleccionados by remember { mutableStateOf<List<PedidoDetalle>>(emptyList()) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos por Cliente", color = Color.White) },
                backgroundColor = Color(0xFF000000),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(clientes) { cliente ->
                Card(
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            clienteSeleccionado = cliente
                            pedidosSeleccionados = pedidoDAO.obtenerHistorialEntregadoPorCliente(cliente.cedula)
                            mostrarDialogo = true
                        },
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${cliente.nombre}", fontWeight = FontWeight.Bold)
                        Text("Cédula: ${cliente.cedula}")
                        Text("Correo: ${cliente.correo}")
                    }
                }
            }
        }

        // Diálogo de pedidos del cliente
        if (mostrarDialogo && clienteSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = {
                    Text("Pedidos de ${clienteSeleccionado!!.nombre}", fontWeight = FontWeight.Bold)
                },
                text = {
                    if (pedidosSeleccionados.isEmpty()) {
                        Text("Este cliente no tiene pedidos entregados.")
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            pedidosSeleccionados.forEach { pedido ->
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Text("Pedido #${pedido.id} - ₡${pedido.total}", fontWeight = FontWeight.Bold)
                                Text("Restaurante: ${pedido.restaurante}")
                                Text("Fecha: ${pedido.horaPedido}")
                                Text("Entregado: ${pedido.horaEntrega ?: "N/A"}")
                                pedido.repartidorNombre?.let {
                                    Text("Repartidor: $it")
                                }
                                Text("Combos:")
                                pedido.combos.forEach {
                                    Text(" - Combo ${it.numero}: ${it.descripcion} (${it.cantidad} x ₡${it.precio})")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

