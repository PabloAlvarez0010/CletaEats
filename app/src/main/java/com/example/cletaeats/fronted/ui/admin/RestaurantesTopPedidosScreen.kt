package com.example.cletaeats.fronted.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.cletaeats.backend.dao.PedidoDAO
import com.example.cletaeats.backend.dao.RestauranteDAO
import com.example.cletaeats.backend.model.Restaurante

private val UberGreen = Color(0xFF06C167)
private val UberGray = Color(0xFFF5F5F5)


@Composable
fun RestaurantesTopPedidosScreen(navController: NavController) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val restauranteDAO = remember { RestauranteDAO(context) }

    var mostrarMas by remember { mutableStateOf(true) }
    var lista by remember { mutableStateOf(emptyList<Pair<Restaurante, Int>>()) }

    LaunchedEffect(mostrarMas) {
        val datos = if (mostrarMas) {
            pedidoDAO.obtenerRestaurantesConMasPedidosEntregados()
        } else {
            pedidoDAO.obtenerRestaurantesConMenosPedidosEntregados()
        }

        lista = datos.mapNotNull { (id, total) ->
            restauranteDAO.obtenerTodos().find { it.id == id }?.let { Pair(it, total) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurantes por Pedidos", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = UberGray
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = if (mostrarMas) "Mostrando: Más pedidos" else "Mostrando: Menos pedidos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = mostrarMas,
                    onCheckedChange = { mostrarMas = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = UberGreen,
                        uncheckedThumbColor = Color.Gray
                    )
                )
            }

            if (lista.isEmpty()) {
                Text("No hay restaurantes con pedidos entregados.")
            } else {
                LazyColumn {
                    items(lista) { (restaurante, cantidad) ->
                        var expand by remember { mutableStateOf(false) }
                        Card(
                            backgroundColor = UberGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { expand = !expand },
                            elevation = 6.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Nombre: ${restaurante.nombre}", color = Color.White, fontSize = 18.sp)
                                Text("Tipo: ${restaurante.tipoComida}", color = Color.White, fontSize = 14.sp)
                                AnimatedVisibility(visible = expand) {
                                    Text("Pedidos entregados: $cantidad", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

