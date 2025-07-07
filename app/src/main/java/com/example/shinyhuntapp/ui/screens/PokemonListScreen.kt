package com.example.shinyhuntapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinyhuntapp.viewmodels.LoginViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@Composable
fun PokemonListScreen(navController: NavController, context: Context) {
    val viewModel: PokemonViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PokemonViewModel(context) as T
            }
        }
    )
    val pokemonList by viewModel.pokemonList.collectAsState()

    // Trigger loading when the screen appears
    LaunchedEffect(Unit) {
        viewModel.fetchPokemon()
        Log.d("PokemonListScreen", "Pokemon list size: ${pokemonList.size}")
    }
    Text("Pokemon List")


    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pokemonList) { pokemon ->
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(16.dp)
            )
        }
        Log.d("PokemonListScreen", "Pokemon list: $pokemonList")
    }
}