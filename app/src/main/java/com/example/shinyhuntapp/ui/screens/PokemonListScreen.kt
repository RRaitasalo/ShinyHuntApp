package com.example.shinyhuntapp.ui.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinyhuntapp.viewmodels.PokemonViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModelFactory

@Composable
fun PokemonListScreen(navController: NavController, viewModel: PokemonViewModel) {

    val pokemonDetailsList by viewModel.pokemonDetailsList.collectAsState()

    // Trigger loading when the screen appears
    LaunchedEffect(Unit) {
        //viewModel.fetchPokemonDetails()
        viewModel.fetchAndStorePokemonIfNeeded()
    }
    Text("Pokemon List")

/*
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pokemonDetailsList) { pokemon ->
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(16.dp)
            )
        }
    }*/
}