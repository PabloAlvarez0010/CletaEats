package com.example.cletaeats.fronted.ui.restaurante

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
import androidx.compose.material.icons.filled.Edit
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
fun RestauranteHomeScreen(navController: NavController, cedulaJuridica: String) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val restauranteDAO = remember { RestauranteDAO(context) }
    val comboDAO = remember { ComboDAO(context) }

    val restaurante by remember {
        mutableStateOf(restauranteDAO.buscarPorCedulaJuridica(cedulaJuridica))
    }

    var combos by remember { mutableStateOf(emptyList<Combo>()) }
    var comboAEditar by remember { mutableStateOf<Combo?>(null) }
    var nuevaDescripcion by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        restaurante?.id?.let {
            combos = comboDAO.obtenerPorRestaurante(it)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Inicio del Restaurante", color = Color.White) },
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
                    Text(restaurante?.nombre ?: "Nombre", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Cédula: ${restaurante?.cedulaJuridica}", color = Color.White, fontSize = 12.sp)
                    Text(restaurante?.direccion ?: "", color = Color.White, fontSize = 12.sp)
                }

                Column(modifier = Modifier.background(UberGreen).fillMaxSize()) {

                    DrawerOptionWithImage(
                        text = "Pedidos pendientes",
                        imageResId = R.drawable.cooking,
                        onClick = {
                            navController.navigate("PedidosPendientesCliente/${restaurante?.id}")
                        }
                    )

                    DrawerOptionWithImage(
                        text = "Pedidos preparados",
                        imageResId = R.drawable.checkfood,
                        onClick = {
                            navController.navigate("pedidosPreparados/${restaurante?.cedulaJuridica}")
                        }
                    )

                    /*
                    * DrawerOptionWithImage(
                        text = "Pedidos pendientes",
                        imageResId = R.drawable.pendingfood,
                        onClick = {
                            navController.navigate("PedidosPendientesCliente/${cliente?.cedula}")
                        }
                    )
                    * */

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
        },
        backgroundColor = UberGray
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(12.dp)) {
            items(combos) { combo ->
                Card(
                    backgroundColor = UberGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = 8.dp
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Combo ${combo.numero}", fontSize = 20.sp, color = Color.White)
                            Text("₡${combo.precio}", color = Color.White)
                            Text(combo.descripcion, color = Color.White)
                        }

                        IconButton(
                            onClick = {
                                comboAEditar = combo
                                nuevaDescripcion = combo.descripcion
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Diálogo para editar descripción
        comboAEditar?.let { combo ->
            AlertDialog(
                onDismissRequest = { comboAEditar = null },
                title = { Text("Editar Descripción") },
                text = {
                    TextField(
                        value = nuevaDescripcion,
                        onValueChange = { nuevaDescripcion = it },
                        label = { Text("Nueva descripción") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val actualizado = combo.copy(descripcion = nuevaDescripcion)
                        comboDAO.actualizar(actualizado)
                        combos = combos.map {
                            if (it.id == combo.id) actualizado else it
                        }
                        comboAEditar = null
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { comboAEditar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

