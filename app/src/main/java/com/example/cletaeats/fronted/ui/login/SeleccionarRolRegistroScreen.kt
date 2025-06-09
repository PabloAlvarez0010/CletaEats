package com.example.cletaeats.fronted.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SeleccionarRolRegistroScreen(navController: NavController) {
    val roles = listOf("cliente", "repartidor", "restaurante")
    var rolSeleccionado by remember { mutableStateOf("cliente") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Selecciona el tipo de usuario", fontWeight = FontWeight.Bold, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        roles.forEach { rol ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = rolSeleccionado == rol,
                    onClick = { rolSeleccionado = rol }
                )
                Text(text = rol.capitalize(), fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (rolSeleccionado) {
                    "cliente" -> navController.navigate("registroCliente")
                    "repartidor" -> navController.navigate("registroRepartidor")
                    "restaurante" -> navController.navigate("registroRestaurante")
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF06C167)),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Continuar", color = Color.White)
        }
    }
}
