package com.example.shinyhuntapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.ui.screens.*
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
        val preferences = PreferenceManager(this)
        val userId = preferences.getLoggedInUserId()
        val startDestination = if (userId != -1) "pokemon_list" else "login"
        //val startDestination = "login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") { LoginScreen(navController, this@MainActivity) }
            composable("register") { RegisterScreen(navController, this@MainActivity) }
            composable("main") { MainScreen(navController) }
            composable("pokemon_list") { PokemonListScreen(navController, this@MainActivity) }
        }
    }
}


