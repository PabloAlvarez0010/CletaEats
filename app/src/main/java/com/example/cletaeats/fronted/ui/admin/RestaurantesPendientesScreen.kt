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
import com.example.cletaeats.backend.dao.RestauranteDAO
import com.example.cletaeats.backend.dao.UsuarioDAO
import com.example.cletaeats.backend.model.Restaurante

@Composable
fun RestaurantesPendientesScreen(navController: NavController) {
    val context = LocalContext.current
    val usuarioDAO = remember { UsuarioDAO(context) }
    val restauranteDAO = remember { RestauranteDAO(context) }

    var restaurantesPendientes by remember { mutableStateOf(emptyList<Restaurante>()) }

    LaunchedEffect(Unit) {
        val cedulas = usuarioDAO.obtenerRestaurantesNoVerificados()
        restaurantesPendientes = cedulas.mapNotNull { restauranteDAO.buscarPorCedulaJuridica(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurantes Pendientes", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (restaurantesPendientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pendientes de verificación.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(restaurantesPendientes) { restaurante ->
                    Card(
                        backgroundColor = Color(0xFF06C167),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${restaurante.nombre}", color = Color.White)
                            Text("Cédula Jurídica: ${restaurante.cedulaJuridica}", color = Color.White)
                            Text("Dirección: ${restaurante.direccion}", color = Color.White)
                            Text("Tipo de comida: ${restaurante.tipoComida}", color = Color.White)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = {
                                    usuarioDAO.actualizarVerificado(restaurante.cedulaJuridica, "si")
                                    restaurantesPendientes = restaurantesPendientes.filterNot { it.cedulaJuridica == restaurante.cedulaJuridica }
                                }) {
                                    Text("Aceptar")
                                }
                                Button(
                                    onClick = {
                                        usuarioDAO.eliminar(restaurante.cedulaJuridica)
                                        restauranteDAO.eliminarPorCedulaJuridica(restaurante.cedulaJuridica)
                                        restaurantesPendientes = restaurantesPendientes.filterNot { it.cedulaJuridica == restaurante.cedulaJuridica }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                                ) {
                                    Text("Rechazar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
