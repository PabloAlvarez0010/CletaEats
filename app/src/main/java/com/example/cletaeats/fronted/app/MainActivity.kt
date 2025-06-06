package com.example.cletaeats.fronted.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cletaeats.fronted.ui.AdminHomeScreen
import com.example.cletaeats.fronted.ui.ClienteHomeScreen
import com.example.cletaeats.fronted.ui.HomeScreen
import com.example.cletaeats.fronted.ui.LoginScreen
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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        // ✅ Ruta para el cliente
        composable(
            route = "clienteHome/{cedula}",
            arguments = listOf(
                navArgument("cedula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cedula = backStackEntry.arguments?.getString("cedula") ?: ""
            ClienteHomeScreen(
                navController = navController,
                cedula = cedula
            )
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
    }
}
