package com.example.cletaeats.fronted.ui.cliente

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cletaeats.backend.dao.ClienteDAO
import com.example.cletaeats.backend.dao.PedidoDAO

private val UberGreen = Color(0xFF06C167)
private val UberBlack = Color(0xFF000000)
private val UberGray = Color(0xFFF5F5F5)

@Composable
fun CarritoScreen(
    navController: NavController,
    cedulaCliente: String,
    carritoViewModel: CarritoViewModel // ← ya no uses = viewModel()
) {
    val context = LocalContext.current
    val clienteDAO = remember { ClienteDAO(context) }
    val pedidoDAO = remember { PedidoDAO(context) }
    val cliente = clienteDAO.buscarPorCedula(cedulaCliente)
    val carrito = carritoViewModel.carrito

    val subtotal = carrito.sumOf { it.first.precio * it.second }
    val costoTransporte = 1500.0
    val iva = subtotal * 0.13
    val total = subtotal + iva + costoTransporte

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras", color = MaterialTheme.colors.onPrimary) },
                backgroundColor = UberBlack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (carrito.isEmpty()) {
                Text("No hay combos en el carrito.")
                return@Column
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(carrito.toList()) { (combo, cantidad) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Combo ${combo.numero}: ${combo.descripcion}", fontWeight = FontWeight.Bold)
                            Text("Precio: ₡${combo.precio}")
                            Text("Cantidad: $cantidad")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Subtotal: ₡${"%.2f".format(subtotal)}")
            Text("IVA (13%): ₡${"%.2f".format(iva)}")
            Text("Transporte: ₡${"%.2f".format(costoTransporte)}")
            Text("Total: ₡${"%.2f".format(total)}", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (cliente?.estado == "activo") {
                        val exito = pedidoDAO.crearPedidoDesdeCarrito(
                            cedulaCliente = cedulaCliente,
                            carrito = carrito,
                            restauranteId = carritoViewModel.restauranteIdActual ?: return@Button,
                            subtotal = subtotal,
                            transporte = costoTransporte,
                            iva = iva,
                            total = total
                        )
                        if (exito) {
                            carritoViewModel.limpiar()
                            Toast.makeText(context, "Pedido creado con éxito", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "No se pudo crear el pedido", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "No puede realizar pedidos: estado ${cliente?.estado ?: "desconocido"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = UberGreen)
            ) {
                Text("Confirmar Pedido", color = Color.White)
            }
        }
    }
}

