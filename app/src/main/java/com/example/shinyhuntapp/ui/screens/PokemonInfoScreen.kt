package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Hunt
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.components.HuntCard
import com.example.shinyhuntapp.viewmodels.PokemonViewModel
import com.example.shinyhuntapp.viewmodels.HuntViewModel


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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ){
            Text(text = "Pokémon Information")
            Text(text = "Name: ${pokemon?.name}")
            Text(text = "National Dex Number: ${pokemon?.nationalDexNumber}")
            Button(
                onClick = { navController.navigate(Routes.huntWithPokemonId(pokemonId)) }
            ) {
                Text(text = "Start Hunt")
            }
            if (hunt != null) {
                Text(text = "Hunt Information")
                HuntCard(
                    hunt = hunt!!,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}