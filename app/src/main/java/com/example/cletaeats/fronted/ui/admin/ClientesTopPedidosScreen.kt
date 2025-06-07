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
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.model.Cliente

private val UberGreen = Color(0xFF06C167)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun ClientesTopPedidosScreen(navController: NavController) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val clienteDAO = remember { ClienteDAO(context) }

    var clientesTop by remember { mutableStateOf<List<Pair<Cliente, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val lista = pedidoDAO.obtenerClientesConMasPedidosEntregados()
        val detallado = lista.mapNotNull { (cedula, cantidad) ->
            clienteDAO.buscarPorCedula(cedula)?.let { cliente -> Pair(cliente, cantidad) }
        }
        clientesTop = detallado
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes con Más Pedidos", color = Color.White) },
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
        if (clientesTop.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos entregados aún.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(clientesTop) { (cliente, cantidad) ->
                    Card(
                        backgroundColor = UberGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${cliente.nombre}", fontSize = 18.sp, color = Color.White)
                            Text("Cédula: ${cliente.cedula}", fontSize = 14.sp, color = Color.White)
                            Text("Correo: ${cliente.correo}", fontSize = 14.sp, color = Color.White)
                            Text("Pedidos entregados: $cantidad", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
