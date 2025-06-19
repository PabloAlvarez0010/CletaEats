package com.example.cletaeats.fronted.ui.cliente

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.R
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.dao.ComboDAO
import com.example.cletaeats.backend.dao.RestauranteDAO
import com.example.cletaeats.backend.model.Combo
import com.example.cletaeats.backend.model.Restaurante
import com.example.cletaeats.fronted.ui.DrawerOption
import com.example.cletaeats.fronted.ui.DrawerOptionWithImage
import kotlinx.coroutines.launch

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun ClienteHomeScreen(navController: NavController, cedula: String, carritoViewModel: CarritoViewModel) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val clienteDAO = remember { ClienteDAO(context) }
    val restauranteDAO = remember { RestauranteDAO(context) }
    val comboDAO = remember { ComboDAO(context) }

    var mostrarDialogoCantidad by remember { mutableStateOf(false) }
    var comboSeleccionado by remember { mutableStateOf<Combo?>(null) }
    var cantidadTexto by remember { mutableStateOf("1") }

    val cliente by remember {
        mutableStateOf(clienteDAO.buscarPorCedula(cedula))
    }

    var restaurantes by remember { mutableStateOf(emptyList<Restaurante>()) }
    var expandedRestauranteId by remember { mutableStateOf<Int?>(null) }
    var combosPorRestaurante by remember { mutableStateOf<Map<Int, List<Combo>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val lista = restauranteDAO.obtenerTodos()
        val mapa = lista.associate { it.id to comboDAO.obtenerPorRestaurante(it.id) }
        restaurantes = lista
        combosPorRestaurante = mapa
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Inicio del Cliente", color = Color.White) },
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
                    Text(cliente?.nombre ?: "Nombre no disponible", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Cédula: ${cliente?.cedula ?: "-"}", color = Color.White, fontSize = 12.sp)
                    Text("Correo: ${cliente?.correo ?: "-"}", color = Color.White, fontSize = 12.sp)
                }

                Column(modifier = Modifier.background(UberGreen).fillMaxSize()) {
                    DrawerOptionWithImage(
                        text = "Pedidos pendientes",
                        imageResId = R.drawable.pendingfood,
                        onClick = {
                            navController.navigate("PedidosPendientesCliente/${cliente?.cedula}")
                        }
                    )
                    DrawerOptionWithImage(
                        text = "Historial de pedidos",
                        imageResId = R.drawable.ordershistory,
                        onClick = {
                            navController.navigate("HistorialPedidosCliente/${cliente?.cedula}")
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
        backgroundColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("carrito/${cliente?.cedula}") },
                backgroundColor = UberGreen,
                contentColor = Color.White
            ) {
                Image(
                    painter = painterResource(id = R.drawable.buy),
                    contentDescription = "Ver carrito",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.cletaeats2),
                contentDescription = null,
                modifier = Modifier.size(560.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.09f
            )

            LazyColumn(modifier = Modifier.padding(padding)) {
                items(restaurantes) { restaurante ->
                    Card(
                        backgroundColor = UberGreen,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                            .clickable {
                                expandedRestauranteId =
                                    if (expandedRestauranteId == restaurante.id) null else restaurante.id
                            },
                        elevation = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(restaurante.nombre, fontSize = 20.sp, color = Color.White)
                            Text(restaurante.tipoComida, fontSize = 14.sp, color = Color.White)
                            Text(restaurante.direccion, fontSize = 14.sp, color = Color.White)

                            AnimatedVisibility(
                                visible = expandedRestauranteId == restaurante.id,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    combosPorRestaurante[restaurante.id]?.forEach { combo ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Combo ${combo.numero}: ${combo.descripcion}", color = Color.White, fontSize = 14.sp)
                                                Text("₡${combo.precio}", color = Color.White, fontSize = 14.sp)
                                            }
                                            Button(
                                                onClick = {
                                                    if (carritoViewModel.restauranteIdActual == null || carritoViewModel.restauranteIdActual == combo.restauranteId) {
                                                        comboSeleccionado = combo
                                                        cantidadTexto = "1"
                                                        mostrarDialogoCantidad = true
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "No puede agregar combos de diferentes restaurantes.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                                            ) {
                                                Text("Agregar", color = UberGreen)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (mostrarDialogoCantidad && comboSeleccionado != null) {
                AlertDialog(
                    onDismissRequest = {
                        mostrarDialogoCantidad = false
                        cantidadTexto = "1"
                    },
                    title = { Text("Cantidad de Combos") },
                    text = {
                        Column {
                            Text("¿Cuántos combos de ${comboSeleccionado?.descripcion} desea agregar?")
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = cantidadTexto,
                                onValueChange = {
                                    if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                                        cantidadTexto = it
                                    }
                                },
                                label = { Text("Cantidad") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val cantidad = cantidadTexto.toIntOrNull()?.coerceIn(1, 10) ?: 1
                            if (cliente?.cedula?.let { clienteDAO.clienteActivo(it) } == true) {
                                repeat(cantidad) {
                                    carritoViewModel.agregarCombo(comboSeleccionado!!)
                                }
                                Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "No puede realizar pedidos porque su cuenta está suspendida",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            mostrarDialogoCantidad = false
                            cantidadTexto = "1"
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = {
                            mostrarDialogoCantidad = false
                            cantidadTexto = "1"
                        }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
