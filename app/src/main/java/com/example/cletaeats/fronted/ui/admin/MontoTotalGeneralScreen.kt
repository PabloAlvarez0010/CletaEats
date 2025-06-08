package com.example.cletaeats.fronted.ui.admin

import androidx.compose.foundation.layout.*
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

private val UberGreen = Color(0xFF06C167)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun MontoTotalGeneralScreen(navController: NavController) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    var montoTotal by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        montoTotal = pedidoDAO.obtenerMontoTotalGeneral()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monto Total General", color = Color.White) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                backgroundColor = UberGreen,
                elevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Monto Total General", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("₡ %.2f".format(montoTotal), fontSize = 24.sp, color = Color.White)
                }
            }
        }
    }
}
