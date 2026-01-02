package com.group22.smartgreenhouse.ui

import LoginActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group22.smartgreenhouse.data.UserRole
import com.group22.smartgreenhouse.data.repository.AuthRepository
import com.group22.smartgreenhouse.ui.register.RegistrationActivity
import com.group22.smartgreenhouse.ui.theme.SmartGreenHouseAppTheme
import com.group22.smartgreenhouse.util.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartGreenHouseAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
                SessionManager.init(applicationContext)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    var role: UserRole = UserRole.USER
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginActivity(
                onLoginClick = { userRole ->
                    role = userRole
                    // Navigate to main screen on successful login
                    navController.navigate("main") {
                        // Clear back stack so user can't go back to login
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                onForgotPasswordClick = {
                    // Handle forgot password navigation if needed
                }
            )
        }
        composable("main") {
            MainScreen(
                userRole = role,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            val authRepository = AuthRepository(LocalContext.current)

            RegistrationActivity(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true } // optional, prevents back nav
                    }
                },
                onLoginClick = {
                    // Handle login navigation
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                authRepository = authRepository
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    //MainScreen()
}


