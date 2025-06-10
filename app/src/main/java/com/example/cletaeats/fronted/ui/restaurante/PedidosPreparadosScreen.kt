package com.example.cletaeats.fronted.ui.restaurante

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
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.dao.RepartidorDAO
import com.example.cletaeats.backend.dao.RestauranteDAO
import com.example.cletaeats.backend.model.Pedido
import com.example.cletaeats.backend.model.Repartidor

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)

@Composable
fun PedidosPreparadosScreen(navController: NavController, cedulaJuridica: String) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val restauranteDAO = remember { RestauranteDAO(context) }
    val repartidorDAO = remember { RepartidorDAO(context) }

    val restaurante = restauranteDAO.buscarPorCedulaJuridica(cedulaJuridica)
    var pedidos by remember { mutableStateOf(emptyList<Pedido>()) }

    LaunchedEffect(Unit) {
        pedidos = pedidoDAO.obtenerPorRestauranteYEstado(restaurante?.id ?: -1, "en camino")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos preparados", color = Color.White) },
                backgroundColor = UberBlack,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            items(pedidos) { pedido ->
                val repartidor: Repartidor? = repartidorDAO.buscarPorCedula(pedido.repartidorId ?: "")
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pedido #${pedido.id}")
                        Text("Cliente: ${pedido.clienteId}")
                        Text("Estado: ${pedido.estado}")
                        Text("Hora pedido: ${pedido.horaPedido}")
                        Text("Repartidor asignado: ${repartidor?.nombre ?: "No disponible"}")
                        Text("Teléfono: ${repartidor?.telefono ?: "-"}")
                        Text("Correo: ${repartidor?.correo ?: "-"}")
                    }
                }
            }
        }
    }
}
