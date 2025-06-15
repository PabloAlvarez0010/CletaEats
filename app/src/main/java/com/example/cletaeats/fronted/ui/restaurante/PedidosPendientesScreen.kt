package com.example.cletaeats.fronted.ui.restaurante

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
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.dao.RepartidorDAO
import com.example.cletaeats.backend.model.Combo
import com.example.cletaeats.backend.model.Pedido


private val UberGreen = Color(0xFF06C167)

@Composable
fun PedidosPendientesScreen(navController: NavController, restauranteId: Int) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val clienteDAO = remember { ClienteDAO(context) }
    val repartidorDAO = remember { RepartidorDAO(context) }

    var pedidos by remember { mutableStateOf(emptyList<Pedido>()) }
    var combosPorPedido by remember { mutableStateOf<Map<Int, List<Pair<Combo, Int>>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        pedidos = pedidoDAO.obtenerPedidosPendientesPorRestaurante(restauranteId)
        combosPorPedido = pedidos.associateBy(
            { it.id },
            { pedidoDAO.obtenerCombosPorPedido(it.id) }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos Pendientes", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (pedidos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos pendientes.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(pedidos) { pedido ->
                    val cliente = remember(pedido.clienteId) {
                        clienteDAO.buscarPorCedula(pedido.clienteId)
                    }

                    val combos = combosPorPedido[pedido.id] ?: emptyList()

                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = 6.dp) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Cliente: ${cliente?.nombre ?: "Desconocido"} (${pedido.clienteId})")
                            Text("Dirección: ${cliente?.direccion ?: "No registrada"}")
                            Text("Teléfono: ${cliente?.telefono ?: "-"}")
                            Text("Correo: ${cliente?.correo ?: "-"}")
                            Text("Hora pedido: ${pedido.horaPedido}")
                            Text("Estado: ${pedido.estado}", fontWeight = FontWeight.SemiBold)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Combos solicitados:", fontWeight = FontWeight.Medium)
                            combos.forEach { (combo, cantidad) ->
                                Text("- Combo ${combo.numero}: ${combo.descripcion} (x$cantidad)")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            var mostrarDialogoNoRepartidores by remember { mutableStateOf(false) }

                            Button(
                                onClick = {
                                    val repartidor = repartidorDAO.obtenerRepartidorDisponible()
                                    if (repartidor != null) {
                                        val exito = pedidoDAO.marcarPedidoEnCamino(pedido.id, repartidor.cedula)
                                        if (exito) {
                                            // Marcar el repartidor como ocupado luego de asignarlo al pedido
                                            repartidorDAO.marcarRepartidorOcupado(repartidor.cedula)

                                            // Refrescar pedidos y combos
                                            pedidos = pedidoDAO.obtenerPedidosPendientesPorRestaurante(restauranteId)
                                            combosPorPedido = pedidos.associateBy(
                                                { it.id },
                                                { pedidoDAO.obtenerCombosPorPedido(it.id) }
                                            )

                                            Toast.makeText(context, "Pedido marcado como 'en camino'", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Error al actualizar pedido", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        mostrarDialogoNoRepartidores = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = UberGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Marcar como 'En camino'", color = Color.White)
                            }


                            if (mostrarDialogoNoRepartidores) {
                                AlertDialog(
                                    onDismissRequest = { mostrarDialogoNoRepartidores = false },
                                    title = { Text("Sin repartidores disponibles") },
                                    text = {
                                        Text("No se puede asignar este pedido porque no hay repartidores disponibles o todos tienen 4 o más amonestaciones.")
                                    },
                                    confirmButton = {
                                        Button(onClick = { mostrarDialogoNoRepartidores = false }) {
                                            Text("Aceptar")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

