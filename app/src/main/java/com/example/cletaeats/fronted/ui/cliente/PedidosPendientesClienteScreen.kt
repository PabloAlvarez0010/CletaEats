package com.example.cletaeats.fronted.ui.cliente

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
import com.example.cletaeats.backend.dao.PedidoDAO

@Composable
fun PedidosPendientesClienteScreen(navController: NavController, cedulaCliente: String) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val historial = remember { pedidoDAO.obtenerHistorialPedidosPorCliente(cedulaCliente).filter { it.estado == "en camino" || it.estado == "en preparación" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos pendientes", color = Color.White) },
                backgroundColor = Color(0xFF000000),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // Regresa a AdminHome
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            if( historial.isEmpty()) {
                item {
                    Text("No hay pedidos en el historial.", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.body1)
                }
            } else {
                items(historial) { pedido ->
                    Card(modifier = Modifier.padding(vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Restaurante: ${pedido.restaurante} (${pedido.tipoComida})", fontWeight = FontWeight.Bold)
                            Text("Estado: ${pedido.estado}")
                            Text("Fecha: ${pedido.horaPedido}")
                            pedido.horaEntrega?.let {
                                Text("Entregado: $it")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Combos:", fontWeight = FontWeight.Bold)
                            pedido.combos.forEach {
                                Text(" - Combo ${it.numero}: ${it.descripcion} (${it.cantidad} x ₡${it.precio})")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Total: ₡${pedido.total}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
