package com.example.cletaeats.fronted.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.RestauranteDAO
import com.example.cletaeats.backend.dao.UsuarioDAO
import com.example.cletaeats.backend.model.Restaurante

@Composable
fun FormularioRestaurante(navController: NavController) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var cedulaJuridica by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipoComida by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Registro de Restaurante", fontWeight = FontWeight.Bold, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cedulaJuridica,
            onValueChange = { cedulaJuridica = it },
            label = { Text("Cédula Jurídica") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del restaurante") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tipoComida,
            onValueChange = { tipoComida = it },
            label = { Text("Tipo de comida") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (cedulaJuridica.isBlank() || nombre.isBlank() || direccion.isBlank() ||
                    tipoComida.isBlank() || clave.isBlank()
                ) {
                    mensaje = "Todos los campos son obligatorios"
                } else {
                    val dao = RestauranteDAO(context)
                    val usuarioDao = UsuarioDAO(context)

                    val existe = dao.buscarPorCedulaJuridica(cedulaJuridica)
                    val existeUsuario = usuarioDao.obtenerPorCedula(cedulaJuridica)

                    if (existe != null || existeUsuario != null) {
                        mensaje = "Ya existe un restaurante con esa cédula"
                    } else {
                        dao.insertar(
                            Restaurante(0, nombre, cedulaJuridica, direccion, tipoComida)
                        )
                        usuarioDao.insertar(cedulaJuridica, clave, "restaurante", "no")
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF06C167))
        ) {
            Text("Registrar Restaurante", color = Color.White)
        }

        if (mensaje.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = mensaje, color = Color.Red)
        }
    }
}

