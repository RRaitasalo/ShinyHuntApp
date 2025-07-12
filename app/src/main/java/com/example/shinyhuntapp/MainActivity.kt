package com.example.shinyhuntapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.screens.DevToolsScreen
import com.example.shinyhuntapp.ui.screens.LoginScreen
import com.example.shinyhuntapp.ui.screens.MainScreen
import com.example.shinyhuntapp.ui.screens.PokemonInfoScreen
import com.example.shinyhuntapp.ui.screens.PokemonListScreen
import com.example.shinyhuntapp.ui.screens.RegisterScreen
import com.example.shinyhuntapp.ui.theme.ShinyHuntAppTheme
import com.example.shinyhuntapp.viewmodels.PokemonViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var pokemonViewModel: PokemonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pokemonViewModel = ViewModelProvider(
            this,
            PokemonViewModelFactory(applicationContext)
        )[PokemonViewModel::class.java]

        setContent {
            ShinyHuntAppTheme {
                AppNavigation(pokemonViewModel)
            }
        }
    }

    @Composable
    fun AppNavigation(pokemonViewModel: PokemonViewModel) {
        val navController = rememberNavController()
        val preferences = PreferenceManager(this)
        val userId = preferences.getLoggedInUserId()
        val startDestination = if (userId != -1) stringResource(R.string.main) else stringResource(R.string.login)
        //val startDestination = "login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable(Routes.LOGIN) { LoginScreen(navController, this@MainActivity) }
            composable(Routes.REGISTER) { RegisterScreen(navController, this@MainActivity) }
            composable(Routes.MAIN) { MainScreen(navController) }
            composable(Routes.POKEMON_LIST) { PokemonListScreen(navController, pokemonViewModel) }
            composable(Routes.DEV_TOOLS) { DevToolsScreen(navController, pokemonViewModel) }
            composable(
                route = "${Routes.POKEMON_INFO}/{pokemonId}",
                arguments = listOf(navArgument("pokemonId") { type = NavType.IntType })
            ) { backStackEntry ->
                val pokemonId = backStackEntry.arguments?.getInt("pokemonId")
                val pokemon = pokemonViewModel.getPokemonById(pokemonId)
                if (pokemon != null) {
                    PokemonInfoScreen(navController, pokemon)
                } else {
                    Text("Pokémon not found")
                }
            }
        }
    }
}


