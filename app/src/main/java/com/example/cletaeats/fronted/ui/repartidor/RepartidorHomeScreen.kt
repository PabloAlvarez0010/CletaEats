package com.example.cletaeats.fronted.ui.repartidor

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.R
import com.example.cletaeats.backend.dao.*
import com.example.cletaeats.backend.model.*
import com.example.cletaeats.fronted.ui.DrawerOption
import com.example.cletaeats.fronted.ui.DrawerOptionWithImage
import kotlinx.coroutines.launch

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun RepartidorHomeScreen(navController: NavController, cedula: String) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val pedidoDAO = remember { PedidoDAO(context) }
    val clienteDAO = remember { ClienteDAO(context) }
    val restauranteDAO = remember { RestauranteDAO(context) }
    val repartidorDAO = remember { RepartidorDAO(context) }

    val repartidor by remember {
        mutableStateOf(repartidorDAO.buscarPorCedula(cedula))
    }

    var pedidosAsignados by remember { mutableStateOf(emptyList<Pedido>()) }

    // Cargar pedidos en camino asignados al repartidor
    LaunchedEffect(Unit) {
        pedidosAsignados = pedidoDAO.obtenerPorRepartidorYEstado(cedula, "en camino")
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Inicio del Repartidor", color = Color.White) },
                backgroundColor = UberBlack,
                navigationIcon = {
                    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                }
            )
        },
        drawerContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                // Perfil
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(UberBlack)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = UberGreen
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Avatar"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(repartidor?.nombre ?: "Nombre no disponible", color = Color.White)
                    Text("Cédula: ${repartidor?.cedula}", color = Color.White, fontSize = 12.sp)
                }

                // Menú lateral
                Column(modifier = Modifier.background(UberGreen).fillMaxSize()) {
                    DrawerOptionWithImage(
                        text = "Historial de pedidos",
                        imageResId = R.drawable.check,
                        onClick = {
                            navController.navigate("historialPedidosRepartidor/${repartidor?.cedula ?: ""}")
                        }
                    )
                    DrawerOptionWithImage(
                        text = "Ver quejas",
                        imageResId = R.drawable.quejas,
                        onClick = {
                            navController.navigate("quejasRepartidor/${repartidor?.cedula ?: ""}")
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Divider()
                    DrawerOption(
                        text = "Cerrar sesión",
                        icon = Icons.Default.ExitToApp,
                        color = Color.Black
                    ) {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        },
        backgroundColor = Color.White
    ) { padding ->
        if (pedidosAsignados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.cletaeats2),
                        contentDescription = "Sin pedidos",
                        modifier = Modifier.size(400.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay pedidos asignados actualmente.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UberBlack
                    )
                    Text(
                        "¡Disfruta un descanso por ahora!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        else {
            LazyColumn(modifier = Modifier
                .padding(padding)
                .padding(12.dp)) {
                items(pedidosAsignados) { pedido ->
                    val cliente = clienteDAO.buscarPorCedula(pedido.clienteId)
                    val restaurante = restauranteDAO.buscarPorId(pedido.restauranteId)

                    Card(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, shape = MaterialTheme.shapes.medium),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Pedido #${pedido.id}", fontSize = 18.sp, color = UberBlack)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Restaurante: ${restaurante?.nombre ?: "-"}", fontSize = 14.sp)
                            Text("Cliente: ${cliente?.nombre ?: "-"}", fontSize = 14.sp)
                            Text("Dirección Cliente: ${cliente?.direccion ?: "-"}", fontSize = 14.sp)
                            Text("Teléfono Cliente: ${cliente?.telefono ?: "-"}", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val exito = pedidoDAO.marcarPedidoEntregado(pedido.id)
                                    if (exito) {
                                        // Marcar repartidor como disponible
                                        pedido.repartidorId?.let { repartidorDAO.marcarRepartidorDisponible(it) }

                                        // Actualizar la lista de pedidos
                                        pedidosAsignados = pedidosAsignados.filter { it.id != pedido.id }

                                        Toast.makeText(context, "Pedido marcado como entregado", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error al marcar el pedido", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = UberGreen),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Marcar como entregado", color = Color.White)
                            }

                        }
                    }
                }
            }
        }
    }
}
