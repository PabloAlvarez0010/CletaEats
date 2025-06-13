package com.example.cletaeats.fronted.ui.cliente

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val pedidosYaCalificados = remember { mutableStateListOf<Int>() }

    // Inicializar lista de pedidos ya calificados
    LaunchedEffect(Unit) {
        historial.forEach {
            if (quejaDAO.existeQuejaPorPedido(it.id)) {
                pedidosYaCalificados.add(it.id)
            }
        }
    }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var descripcion by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf(0) }
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
        if (historial.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos entregados aún.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(historial) { pedido ->
                    val yaExiste = pedido.id in pedidosYaCalificados

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
                                    repartidorActual = pedido.repartidorCedula
                                    pedidoActualId = pedido.id
                                    mostrarDialogo = true
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

        // Diálogo emergente
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
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            for (i in 1..5) {
                                IconToggleButton(
                                    checked = i <= calificacion,
                                    onCheckedChange = {
                                        calificacion = i
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (i <= calificacion) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = "Estrella $i",
                                        tint = if (i <= calificacion) Color(0xFFFFC107) else Color.Gray
                                    )
                                }
                            }
                        }
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
                                calificacion = calificacion,
                                pedidoId = pedidoActualId
                            )
                            val exito = quejaDAO.insertar(queja)
                            if (exito) {
                                Toast.makeText(context, "Calificación enviada", Toast.LENGTH_SHORT).show()
                                pedidosYaCalificados.add(pedidoActualId!!)
                            } else {
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                        }
                        mostrarDialogo = false
                        descripcion = ""
                        calificacion = 0
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
