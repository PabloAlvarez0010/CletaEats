package com.example.cletaeats.fronted.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.R
import com.example.cletaeats.fronted.ui.DrawerOption
import kotlinx.coroutines.launch

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)

@Composable
fun AdminHomeScreen(navController: NavController, nombre: String, cedula: String) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var clienteExpanded by remember { mutableStateOf(false) }
    var restauranteExpanded by remember { mutableStateOf(false) }
    var repartidorExpanded by remember { mutableStateOf(false) }
    var pedidoExpanded by remember { mutableStateOf(false) }
    var reporteExpanded by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Administrador", color = Color.White) },
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
                    Text(nombre, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Cédula: $cedula", color = Color.White, fontSize = 12.sp)
                }

                Column(modifier = Modifier.background(UberGreen).fillMaxSize()) {
                    ExpandableDrawerOption(
                        navController = navController,
                        title = "Clientes",
                        expanded = clienteExpanded,
                        onToggle = { clienteExpanded = !clienteExpanded },
                        subOptions = listOf(
                            "Incluir cliente",
                            "Listar clientes activos/suspendidos",
                            "Cliente con más pedidos"
                        ),
                        iconResId = R.drawable.client
                    )

                    ExpandableDrawerOption(
                        navController = navController,
                        title = "Restaurantes",
                        expanded = restauranteExpanded,
                        onToggle = { restauranteExpanded = !restauranteExpanded },
                        subOptions = listOf(
                            "Incluir restaurante",
                            "Restaurante con más/menos pedidos",
                            "Monto total por restaurante",
                            "Monto total general"
                        ),
                        iconResId = R.drawable.restaurant
                    )

                    ExpandableDrawerOption(
                        navController = navController,
                        title = "Repartidores",
                        expanded = repartidorExpanded,
                        onToggle = { repartidorExpanded = !repartidorExpanded },
                        subOptions = listOf(
                            "Incluir repartidor",
                            "Listar sin amonestaciones",
                            "Quejas por repartidor"
                        ),
                        iconResId = R.drawable.bycicle
                    )

                    ExpandableDrawerOption(
                        navController = navController,
                        title = "Pedidos",
                        expanded = pedidoExpanded,
                        onToggle = { pedidoExpanded = !pedidoExpanded },
                        subOptions = listOf(
                            "Pedidos por cliente",
                            "Cliente con más pedidos realizados",
                            "Hora pico"
                        ),
                        iconResId = R.drawable.orders
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
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cletaeats2),
                    contentDescription = "Logo CletaEats",
                    modifier = Modifier
                        .size(560.dp)
                        .padding(bottom = 24.dp)
                )
                Text(
                    "Seleccione una opción del menú",
                    fontSize = 20.sp,
                    color = UberBlack,
                    fontWeight = FontWeight.Bold
                )
            }
        }


    }
}

@Composable
fun ExpandableDrawerOption(
    navController: NavController,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    subOptions: List<String>,
    iconResId: Int? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconResId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = Color.White
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 32.dp)) {
                subOptions.forEach { item ->
                    DrawerOption(text = item) {
                        when (item) {
                            "Listar clientes activos/suspendidos" -> {
                                navController.navigate("clientesActivosSuspendidos")
                            }
                            "Cliente con más pedidos" -> {
                                navController.navigate("clientesTopPedidos")
                            }
                            "Listar sin amonestaciones" -> {
                                navController.navigate("repartidoresSinAmonestaciones")
                            }
                            "Quejas por repartidor" -> {
                                navController.navigate("quejasPorRepartidor")
                            }
                            "Restaurante con más/menos pedidos" -> {
                                navController.navigate("restaurantesTopPedidos")
                            }
                            "Monto total por restaurante" -> {
                                navController.navigate("montoTotalPorRestaurante")
                            }
                            "Monto total general" -> {
                                navController.navigate("montoTotalGeneral")
                            }
                            "Incluir cliente" -> {
                                navController.navigate("clientesPendientes")
                            }
                            "Incluir restaurante" -> {
                                navController.navigate("restaurantesPendientes")
                            }
                            "Incluir repartidor" -> {
                                navController.navigate("repartidoresPendientes")
                            }
                            "Pedidos por cliente" -> {
                                navController.navigate("pedidosPorCliente")
                            }
                            "Cliente con más pedidos realizados" -> {
                                navController.navigate("clientesTopPedidos2")
                            }
                            "Hora pico" -> {
                                navController.navigate("horaPico")
                            }
                            else -> {
                                println("Seleccionado: $item")
                            }
                        }
                    }
                }
            }
        }
    }
}
