package com.example.shinyhuntapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shinyhuntapp.ui.screens.LoginScreen
import com.example.shinyhuntapp.ui.screens.MainScreen
import com.example.shinyhuntapp.ui.screens.RegisterScreen
import com.example.shinyhuntapp.ui.theme.ShinyHuntAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShinyHuntAppTheme {
                AppNavigation()
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("logged_in_user_id", -1)
        //val startDestination = if (userId != -1) "main" else "login"
        val startDestination = "login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") { LoginScreen(navController, this@MainActivity) }
            composable("register") { RegisterScreen(navController, this@MainActivity) }
            composable("main") { MainScreen(navController) }
        }
    }
}


