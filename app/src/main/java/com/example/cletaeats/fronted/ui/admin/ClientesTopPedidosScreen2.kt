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
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.model.Cliente

private val UberGreen = Color(0xFF06C167)
private val UberGray = Color(0xFFF5F5F5)
@Composable
fun ClientesTopPedidosScreen2(navController: NavController) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }

    val topClientes = remember { pedidoDAO.obtenerClientesConMasPedidos() }

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
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            if (topClientes.isEmpty()) {
                item {
                    Text("No hay pedidos entregados.", style = MaterialTheme.typography.body1)
                }
            } else {
                items(topClientes) { cliente ->
                    Card(
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Cliente: ${cliente.clienteNombre}", fontWeight = FontWeight.Bold)
                            Text("Pedidos entregados: ${cliente.cantidadPedidos}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Restaurantes:", fontWeight = FontWeight.SemiBold)
                            cliente.restaurantes.forEach {
                                Text("• $it")
                            }
                        }
                    }
                }
            }
        }
    }
}
