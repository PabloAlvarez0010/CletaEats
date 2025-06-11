package com.example.cletaeats.fronted.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cletaeats.fronted.ui.admin.AdminHomeScreen
import com.example.cletaeats.fronted.ui.admin.ClientesPendientesScreen
import com.example.cletaeats.fronted.ui.cliente.CarritoScreen
import com.example.cletaeats.fronted.ui.cliente.ClienteHomeScreen
import com.example.cletaeats.fronted.ui.login.LoginScreen
import com.example.cletaeats.fronted.ui.admin.ClientesTopPedidosScreen
import com.example.cletaeats.fronted.ui.admin.ListaClientesScreen
import com.example.cletaeats.fronted.ui.admin.MontoTotalGeneralScreen
import com.example.cletaeats.fronted.ui.admin.MontoTotalPorRestauranteScreen
import com.example.cletaeats.fronted.ui.admin.QuejasPorRepartidorScreen
import com.example.cletaeats.fronted.ui.admin.RepartidoresPendientesScreen
import com.example.cletaeats.fronted.ui.admin.RepartidoresSinAmonestacionesScreen
import com.example.cletaeats.fronted.ui.admin.RestaurantesPendientesScreen
import com.example.cletaeats.fronted.ui.admin.RestaurantesTopPedidosScreen
import com.example.cletaeats.fronted.ui.cliente.CarritoViewModel
import com.example.cletaeats.fronted.ui.cliente.HistorialPedidosClienteScreen
import com.example.cletaeats.fronted.ui.cliente.PedidosPendientesClienteScreen
import com.example.cletaeats.fronted.ui.login.FormularioCliente
import com.example.cletaeats.fronted.ui.login.FormularioRepartidor
import com.example.cletaeats.fronted.ui.login.FormularioRestaurante
import com.example.cletaeats.fronted.ui.login.SeleccionarRolRegistroScreen
import com.example.cletaeats.fronted.ui.repartidor.HistorialPedidosRepartidorScreen
import com.example.cletaeats.fronted.ui.repartidor.RepartidorHomeScreen
import com.example.cletaeats.fronted.ui.restaurante.PedidosPendientesScreen
import com.example.cletaeats.fronted.ui.restaurante.PedidosPreparadosScreen
import com.example.cletaeats.fronted.ui.restaurante.RestauranteHomeScreen
import com.example.cletaeats.ui.theme.CletaEatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CletaEatsTheme {
                Surface {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel() // Instancia compartida del carrito

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }

        composable(
            route = "clienteHome/{cedula}",
            arguments = listOf(navArgument("cedula") { type = NavType.StringType })
        ) { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedula") ?: ""
            ClienteHomeScreen(
                navController = navController,
                cedula = cedula,
                carritoViewModel = carritoViewModel // ← se pasa
            )
        }

        composable(
            route = "carrito/{cedulaCliente}",
            arguments = listOf(navArgument("cedulaCliente") { type = NavType.StringType })
        ) { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedulaCliente") ?: ""
            CarritoScreen(
                navController = navController,
                cedulaCliente = cedula,
                carritoViewModel = carritoViewModel // ← se pasa
            )
        }

        // ✅ Ruta para el administrador
        composable(
            route = "adminHome/{cedula}",
            arguments = listOf(
                navArgument("cedula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedula") ?: ""
            AdminHomeScreen(
                navController = navController,
                nombre = "Administrador",
                cedula = cedula
            )
        }
        // ✅ Ruta para el restaurante
        composable(
            route = "restauranteHome/{cedulaJuridica}",
            arguments = listOf(
                navArgument("cedulaJuridica") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cedulaJuridica = backStackEntry.arguments?.getString("cedulaJuridica") ?: ""
            RestauranteHomeScreen(
                navController = navController,
                cedulaJuridica = cedulaJuridica
            )
        }

        composable(
            route = "repartidorHome/{cedula}",
            arguments = listOf(navArgument("cedula") { type = NavType.StringType })
        ) { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedula") ?: ""
            RepartidorHomeScreen(navController, cedula)
        }

        composable("clientesActivosSuspendidos") {
            ListaClientesScreen(navController)
        }
        composable("clientesTopPedidos") {
            ClientesTopPedidosScreen(navController)
        }
        composable("repartidoresSinAmonestaciones") {
            RepartidoresSinAmonestacionesScreen(navController)
        }
        composable("quejasPorRepartidor") {
            QuejasPorRepartidorScreen(navController)
        }
        composable("restaurantesTopPedidos") {
            RestaurantesTopPedidosScreen(navController)
        }
        composable("montoTotalPorRestaurante") {
            MontoTotalPorRestauranteScreen(navController)
        }
        composable("montoTotalGeneral") {
            MontoTotalGeneralScreen(navController)
        }
        composable("registroUsuario") {
            SeleccionarRolRegistroScreen(navController)
        }
        composable("registroCliente") {
            FormularioCliente(navController)
        }
        composable("registroRepartidor") {
            FormularioRepartidor(navController)
        }
        composable("registroRestaurante") {
            FormularioRestaurante(navController)
        }
        composable("clientesPendientes") {
            ClientesPendientesScreen(navController)
        }
        composable("repartidoresPendientes") {
           RepartidoresPendientesScreen(navController)
        }
        composable("restaurantesPendientes") {
          RestaurantesPendientesScreen(navController)
        }
        composable("PedidosPendientesCliente/{cedulaCliente}") { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedulaCliente") ?: ""
            PedidosPendientesClienteScreen(navController, cedula)
        }
        composable("historialPedidosCliente/{cedulaCliente}") { backStackEntry ->
            val cedulaCliente = backStackEntry.arguments?.getString("cedulaCliente") ?: ""
            HistorialPedidosClienteScreen(navController, cedulaCliente)
        }





        composable(
            route = "pedidosPendientes/{restauranteId}",
            arguments = listOf(navArgument("restauranteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restauranteId = backStackEntry.arguments?.getInt("restauranteId") ?: 0
            PedidosPendientesScreen(navController, restauranteId)
        }
        composable(
            route = "pedidosPreparados/{cedulaJuridica}",
            arguments = listOf(navArgument("cedulaJuridica") { type = NavType.StringType })
        ) { backStackEntry ->
            val cedulaJuridica = backStackEntry.arguments?.getString("cedulaJuridica") ?: ""
            PedidosPreparadosScreen(navController, cedulaJuridica)
        }
        composable(
            route = "historialPedidosRepartidor/{cedula}",
            arguments = listOf(navArgument("cedula") { type = NavType.StringType })
        ) { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedula") ?: ""
            HistorialPedidosRepartidorScreen(cedulaRepartidor = cedula, navController = navController)
        }



        /*composable(
            route = "home/{rol}/{cedula}",
            arguments = listOf(
                navArgument("rol") { type = NavType.StringType },
                navArgument("cedula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: ""
            val cedula = backStackEntry.arguments?.getString("cedula") ?: ""
            HomeScreen(rol, cedula)
        }*/

    }
}
