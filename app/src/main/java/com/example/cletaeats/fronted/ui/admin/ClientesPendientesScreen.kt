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
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.dao.UsuarioDAO
import com.example.cletaeats.backend.model.Cliente


@Composable
fun ClientesPendientesScreen(navController: NavController) {
    val context = LocalContext.current
    val clienteDAO = remember { ClienteDAO(context) }
    val usuarioDAO = remember { UsuarioDAO(context) }

    var clientesPendientes by remember { mutableStateOf(emptyList<Cliente>()) }

    LaunchedEffect(Unit) {
        val cedulas = usuarioDAO.obtenerClientesNoVerificados()
        clientesPendientes = cedulas.mapNotNull { clienteDAO.buscarPorCedula(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes Pendientes", color = Color.White) },
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (clientesPendientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay clientes pendientes de verificación.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(clientesPendientes) { cliente ->
                    Card(
                        backgroundColor = Color(0xFF06C167),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${cliente.nombre}", color = Color.White)
                            Text("Cédula: ${cliente.cedula}", color = Color.White)
                            Text("Correo: ${cliente.correo}", color = Color.White)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = {
                                    usuarioDAO.actualizarVerificado(cliente.cedula, "sí")
                                    clientesPendientes = clientesPendientes.filterNot { it.cedula == cliente.cedula }
                                }) {
                                    Text("Aceptar")
                                }
                                Button(
                                    onClick = {
                                        usuarioDAO.eliminar(cliente.cedula)
                                        clienteDAO.eliminar(cliente.cedula)
                                        clientesPendientes = clientesPendientes.filterNot { it.cedula == cliente.cedula }
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
