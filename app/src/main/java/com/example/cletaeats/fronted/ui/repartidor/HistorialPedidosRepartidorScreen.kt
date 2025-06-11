package com.example.cletaeats.fronted.ui.repartidor

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
import com.example.cletaeats.backend.dao.*
import com.example.cletaeats.backend.model.*

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun HistorialPedidosRepartidorScreen(cedulaRepartidor: String, navController: NavController) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val restauranteDAO = remember { RestauranteDAO(context) }
    val clienteDAO = remember { ClienteDAO(context) }

    var pedidos by remember { mutableStateOf(emptyList<Pedido>()) }

    LaunchedEffect(Unit) {
        pedidos = pedidoDAO.obtenerPedidosEntregadosPorRepartidor(cedulaRepartidor)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pedidos", color = Color.White) },
                backgroundColor = UberBlack,
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
        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            items(pedidos) { pedido ->
                val restaurante = restauranteDAO.buscarPorId(pedido.restauranteId)
                val cliente = clienteDAO.buscarPorCedula(pedido.clienteId)

                Card(
                    elevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold)
                        Text("Estado: ${pedido.estado}")
                        Text("Hora pedido: ${pedido.horaPedido}")
                        Text("Hora entrega: ${pedido.horaEntrega ?: "No registrada"}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Restaurante: ${restaurante?.nombre ?: "-"}")
                        Text("Cliente: ${cliente?.nombre ?: "-"}")
                    }
                }
            }
        }
    }
}
