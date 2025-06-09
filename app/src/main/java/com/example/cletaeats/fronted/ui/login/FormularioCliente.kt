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
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.dao.UsuarioDAO
import com.example.cletaeats.backend.model.Cliente

@Composable
fun FormularioCliente(navController: NavController) {
    val context = LocalContext.current

    var cedula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tarjeta by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Registro de Cliente", fontWeight = FontWeight.Bold, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it },
            label = { Text("Cédula") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tarjeta,
            onValueChange = { tarjeta = it },
            label = { Text("Número de tarjeta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (cedula.isBlank() || nombre.isBlank() || direccion.isBlank() ||
                    tarjeta.isBlank() || telefono.isBlank() || correo.isBlank() || clave.isBlank()
                ) {
                    mensaje = "Todos los campos son obligatorios"
                } else {
                    val clienteDAO = ClienteDAO(context)
                    val usuarioDAO = UsuarioDAO(context)

                    val clienteExistente = clienteDAO.buscarPorCedula(cedula)
                    val usuarioExistente = usuarioDAO.obtenerPorCedula(cedula)

                    if (clienteExistente != null || usuarioExistente != null) {
                        mensaje = "Ya existe un usuario con esa cédula"
                    } else {
                        clienteDAO.insertar(
                            Cliente(cedula, nombre, direccion, tarjeta, telefono, correo, "activo")
                        )
                        usuarioDAO.insertar(cedula, clave, "cliente", "no")
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF06C167))
        ) {
            Text("Registrar Cliente", color = Color.White)
        }

        if (mensaje.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = mensaje, color = Color.Red)
        }
    }
}

