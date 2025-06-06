package com.example.cletaeats.fronted.ui

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
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import com.example.cletaeats.fronted.ui.DrawerOption


private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)


@Composable
fun AdminHomeScreen(navController: NavController, nombre: String, cedula: String) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // Estados para los submenús
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
                // Sección superior
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

                // Opciones expandibles
                Column(modifier = Modifier.background(UberGreen).fillMaxSize()) {
                    ExpandableDrawerOption(
                        title = "Clientes",
                        expanded = clienteExpanded,
                        onToggle = { clienteExpanded = !clienteExpanded },
                        subOptions = listOf(
                            "Incluir cliente",
                            "Listar clientes activos",
                            "Listar clientes suspendidos",
                            "Cliente con más pedidos"
                        )
                    )

                    ExpandableDrawerOption(
                        title = "Restaurantes",
                        expanded = restauranteExpanded,
                        onToggle = { restauranteExpanded = !restauranteExpanded },
                        subOptions = listOf(
                            "Incluir restaurante",
                            "Restaurante con más pedidos",
                            "Restaurante con menos pedidos",
                            "Monto total por restaurante",
                            "Monto total general"
                        )
                    )

                    ExpandableDrawerOption(
                        title = "Repartidores",
                        expanded = repartidorExpanded,
                        onToggle = { repartidorExpanded = !repartidorExpanded },
                        subOptions = listOf(
                            "Incluir repartidor",
                            "Listar sin amonestaciones",
                            "Quejas por repartidor"
                        )
                    )

                    ExpandableDrawerOption(
                        title = "Pedidos",
                        expanded = pedidoExpanded,
                        onToggle = { pedidoExpanded = !pedidoExpanded },
                        subOptions = listOf(
                            "Incluir pedido",
                            "Pedidos por cliente",
                            "Hora pico"
                        )
                    )

                    ExpandableDrawerOption(
                        title = "Reportes",
                        expanded = reporteExpanded,
                        onToggle = { reporteExpanded = !reporteExpanded },
                        subOptions = listOf(
                            "Generar reporte general",
                            "Generar rúbrica"
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Divider()

                    DrawerOption(
                        text = "Cerrar sesión",
                        icon = Icons.Default.ExitToApp,
                        color = Color.Red
                    ) {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        }
    ) {
        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Text("Seleccione una opción del menú", fontSize = 18.sp)
        }
    }
}

@Composable
fun ExpandableDrawerOption(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    subOptions: List<String>
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
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
                        println("Seleccionado: $item")
                    }
                }
            }
        }
    }
}

