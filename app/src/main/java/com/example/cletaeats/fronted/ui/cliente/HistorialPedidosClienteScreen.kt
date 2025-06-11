package com.example.cletaeats.fronted.ui.cliente

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
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.PedidoDAO

@Composable
fun HistorialPedidosClienteScreen(navController: NavController, cedulaCliente: String) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val historial = remember {
        pedidoDAO.obtenerHistorialEntregadoPorCliente(cedulaCliente)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pedidos", color = Color.White) },
                backgroundColor = Color(0xFF000000),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (historial.isEmpty()) {
                item {
                    Text("No hay pedidos entregados aún.", style = MaterialTheme.typography.body1)
                }
            } else {
                items(historial) { pedido ->
                    Card(modifier = Modifier.padding(vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Restaurante: ${pedido.restaurante} (${pedido.tipoComida})", fontWeight = FontWeight.Bold)
                            Text("Estado: ${pedido.estado}")
                            Text("Fecha de pedido: ${pedido.horaPedido}")
                            Text("Fecha de entrega: ${pedido.horaEntrega ?: "N/A"}")

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Combos:", fontWeight = FontWeight.Bold)
                            pedido.combos.forEach {
                                Text(" - Combo ${it.numero}: ${it.descripcion} (${it.cantidad} x ₡${it.precio})")
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Total: ₡${pedido.total}", fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Repartidor: ${pedido.repartidorNombre ?: "No asignado"}")

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    // Aquí se colocará el diálogo de calificación en una etapa posterior
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF06C167)),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Calificar Repartidor", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
