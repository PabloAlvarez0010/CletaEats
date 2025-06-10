package com.example.cletaeats.fronted.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.UsuarioDAO

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    var cedula by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = UberGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CletaEats",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = UberBlack
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = cedula,
                onValueChange = { cedula = it },
                label = { Text("Cédula") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (cedula.isBlank() || clave.isBlank()) {
                        error = "Por favor complete todos los campos"
                    } else {
                        val usuario = UsuarioDAO(context).autenticar(cedula, clave)
                        if (usuario == null) {
                            error = "Cédula o clave incorrecta"
                        } else if (usuario.verificado.lowercase() != "si") {
                            error = "Usuario pendiente de verificación por el administrador"
                        } else {
                            when (usuario.rol.lowercase()) {
                                "admin" -> navController.navigate("adminHome/${usuario.cedula}")
                                "cliente" -> navController.navigate("clienteHome/${usuario.cedula}")
                                "restaurante" -> navController.navigate("restauranteHome/${usuario.cedula}")
                                else -> navController.navigate("home/${usuario.rol}/${usuario.cedula}")
                            }
                            error = null
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = UberGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ingresar", color = Color.White, fontSize = 18.sp)
            }

            TextButton(onClick = { navController.navigate("registroUsuario") }) {
                Text("¿No tienes cuenta? Regístrate aquí", color = UberBlack)
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = Color.Red)
            }
        }
    }
}
