package com.example.cletaeats.fronted.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.PedidoDAO

@Composable
fun HoraPicoScreen(navController: NavController) {
    val context = LocalContext.current
    val pedidoDAO = remember { PedidoDAO(context) }
    val horaPico = remember { pedidoDAO.obtenerHoraPico() }
    val datosPorHora = remember { pedidoDAO.obtenerPedidosPorHora() }

    val gradiente = Brush.verticalGradient(
        colors = listOf(Color(0xFF06C167), Color(0xFF028C4E))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hora Pico de Pedidos", color = Color.White) },
                backgroundColor = Color(0xFF000000),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = Color(0xFFF2F2F2)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 36.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Estadísticas del Día",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (horaPico == null || datosPorHora.isEmpty()) {
                Card(
                    backgroundColor = Color.White,
                    elevation = 6.dp,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No hay pedidos registrados.", style = MaterialTheme.typography.body1)
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(gradiente),
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .background(gradiente),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆 Hora Pico", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("${horaPico.first}:00 hrs", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pedidos registrados: ${horaPico.second}", fontSize = 18.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text("Distribución por hora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                GraficoBarras(datosPorHora)
            }
        }
    }
}

@Composable
fun GraficoBarras(datos: Map<Int, Int>) {
    val maxValor = datos.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        datos.toSortedMap().forEach { (hora, cantidad) ->
            val porcentaje = cantidad.toFloat() / maxValor
            val colorBarra = if (cantidad == maxValor) Color(0xFF06C167) else Color(0xFFB2DFDB)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "${hora}:00",
                    modifier = Modifier.width(56.dp),
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(porcentaje)
                        .height(22.dp)
                        .background(colorBarra, shape = RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "$cantidad", fontSize = 14.sp)
            }
        }
    }
}
