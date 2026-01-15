package com.example.shinyhuntapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.viewmodels.LoginViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController, pokemonViewModel: PokemonViewModel, loginViewModel: LoginViewModel) {

    LaunchedEffect(Unit) {
        pokemonViewModel.fetchAndStorePokemonIfNeeded()
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = currentRoute == Routes.MAIN,
                    onClick = { navController.navigate(Routes.MAIN) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.pokemon_list)) },
                    label = { Text(stringResource(R.string.pokedex)) },
                    selected = currentRoute == Routes.POKEMON_LIST,
                    onClick = { navController.navigate(Routes.POKEMON_LIST) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.hunt)) },
                    label = { Text(stringResource(R.string.hunt)) },
                    selected = currentRoute == Routes.HUNT,
                    onClick = { navController.navigate(Routes.HUNT) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentRoute == Routes.SETTINGS,
                    onClick = { navController.navigate(Routes.SETTINGS) }
                )
            }
        }
    ) { innerPadding ->
        // Content area remains empty for now
        Modifier.padding(innerPadding)
    }
}
