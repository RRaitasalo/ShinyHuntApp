package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@Composable
fun PokemonListScreen(navController: NavController, viewModel: PokemonViewModel) {

    val pokemonList by viewModel.pokemonList.collectAsState()

    // Trigger loading when the screen appears
    LaunchedEffect(Unit) {
        viewModel.fetchAndStorePokemonIfNeeded()
        viewModel.fetchPokemonList()
    }
    Text(stringResource(R.string.pokemon_list))


    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pokemonList) { pokemon ->
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}