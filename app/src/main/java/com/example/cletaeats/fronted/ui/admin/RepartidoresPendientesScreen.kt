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
import com.example.cletaeats.backend.dao.RepartidorDAO
import com.example.cletaeats.backend.dao.UsuarioDAO
import com.example.cletaeats.backend.model.Repartidor

@Composable
fun RepartidoresPendientesScreen(navController: NavController) {
    val context = LocalContext.current
    val usuarioDAO = remember { UsuarioDAO(context) }
    val repartidorDAO = remember { RepartidorDAO(context) }

    var repartidoresPendientes by remember { mutableStateOf(emptyList<Repartidor>()) }

    LaunchedEffect(Unit) {
        val cedulas = usuarioDAO.obtenerRepartidoresNoVerificados()
        repartidoresPendientes = cedulas.mapNotNull { repartidorDAO.buscarPorCedula(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repartidores Pendientes", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (repartidoresPendientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay repartidores pendientes de verificación.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(repartidoresPendientes) { repartidor ->
                    Card(
                        backgroundColor = Color(0xFF06C167),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${repartidor.nombre}", color = Color.White)
                            Text("Cédula: ${repartidor.cedula}", color = Color.White)
                            Text("Correo: ${repartidor.correo}", color = Color.White)
                            Text("Teléfono: ${repartidor.telefono}", color = Color.White)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = {
                                    usuarioDAO.actualizarVerificado(repartidor.cedula, "sí")
                                    repartidoresPendientes = repartidoresPendientes.filterNot { it.cedula == repartidor.cedula }
                                }) {
                                    Text("Aceptar")
                                }
                                Button(
                                    onClick = {
                                        usuarioDAO.eliminar(repartidor.cedula)
                                        repartidorDAO.eliminar(repartidor.cedula)
                                        repartidoresPendientes = repartidoresPendientes.filterNot { it.cedula == repartidor.cedula }
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
