package com.example.cletaeats.fronted.ui.cliente

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
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.dao.QuejaDAO
import com.example.cletaeats.backend.model.Queja
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistorialPedidosClienteScreen(navController: NavController, cedulaCliente: String) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val quejaDAO = remember { QuejaDAO(context) }

    val historial = remember { pedidoDAO.obtenerHistorialEntregadoPorCliente(cedulaCliente) }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var descripcion by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf(0f) }
    var repartidorActual by remember { mutableStateOf<String?>(null) }
    var pedidoActualId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pedidos", color = Color.White) },
                backgroundColor = Color(0xFF000000),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                    val yaExiste = remember { quejaDAO.existeQuejaPorPedido(pedido.id) }

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
                                    if (yaExiste) {
                                        Toast.makeText(context, "Ya se calificó este pedido.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        repartidorActual = pedido.repartidorCedula
                                        pedidoActualId = pedido.id
                                        mostrarDialogo = true
                                    }
                                },
                                enabled = !yaExiste,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (yaExiste) Color.Gray else Color(0xFF06C167)
                                ),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Calificar Repartidor", color = Color.White)
                            }

                            if (yaExiste) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Ya calificado", color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize)
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Calificar Repartidor") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Comentario") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Calificación:")
                        Slider(
                            value = calificacion,
                            onValueChange = { calificacion = it },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                        Text("Valor: ${calificacion.toInt()}")
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (descripcion.isNotBlank() && repartidorActual != null && pedidoActualId != null) {
                            val queja = Queja(
                                repartidorId = repartidorActual!!,
                                clienteId = cedulaCliente,
                                descripcion = descripcion,
                                fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                calificacion = calificacion.toInt(),
                                pedidoId = pedidoActualId
                            )
                            val exito = quejaDAO.insertar(queja)
                            if (exito) {
                                Toast.makeText(context, "Calificación enviada", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                        }
                        mostrarDialogo = false
                        descripcion = ""
                        calificacion = 0f
                    }) {
                        Text("Enviar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
