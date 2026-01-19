package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.components.HuntCard
import com.example.shinyhuntapp.viewmodels.HuntViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonInfoScreen(
    navController: NavController,
    pokemonId: Int,
    pokemonViewModel: PokemonViewModel,
    huntViewModel: HuntViewModel
) {
    var pokemon by remember { mutableStateOf<Pokemon?>(null) }
    val hunt by huntViewModel.huntForPokemon.collectAsState()

    LaunchedEffect(pokemonId) {
        pokemonViewModel.getPokemonById(pokemonId) {
            pokemon = it
        }
        huntViewModel.getHuntForPokemon(pokemonId)
    }

    val currentRouteForNav = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(
                    text = pokemon?.name.toString(),
                    style = MaterialTheme.typography.headlineMedium
                )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = currentRouteForNav == Routes.MAIN,
                    onClick = { navController.navigate(Routes.MAIN) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = stringResource(R.string.pokemon_list)) },
                    label = { Text(stringResource(R.string.pokedex)) },
                    selected = true,  // As per user request
                    onClick = { navController.navigate(Routes.POKEMON_LIST) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.hunt)) },
                    label = { Text(stringResource(R.string.hunt)) },
                    selected = currentRouteForNav?.startsWith(Routes.HUNT) == true,
                    onClick = { navController.navigate(Routes.HUNT) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentRouteForNav == Routes.SETTINGS,
                    onClick = { navController.navigate(Routes.SETTINGS) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ){
            Text(text = stringResource(R.string.pokemon_information))
            Text(text = stringResource(R.string.name_of, pokemon?.name.toString()))
            Text(text = stringResource(R.string.national_dex_number, pokemon?.nationalDexNumber.toString()))
            if (hunt != null) {
                Text(text = stringResource(R.string.hunt_information))
                HuntCard(
                    navController = navController,
                    hunt = hunt!!,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = { navController.navigate(Routes.huntWithPokemonId(pokemonId)) }
                ) {
                    Text(text = if (hunt?.isFoundShiny == true) stringResource(R.string.start_new_hunt) else stringResource(R.string.continue_hunt))
                }
            } else {
                Button(
                    onClick = {
                        navController.navigate(Routes.huntWithPokemonId(pokemonId))
                    }
                ) {
                    Text(text = stringResource(R.string.start_new_hunt))
                }
            }
        }
    }
}