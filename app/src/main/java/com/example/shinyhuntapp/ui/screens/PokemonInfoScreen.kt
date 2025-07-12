package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Pokemon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonInfoScreen(navController: NavController, pokemon: Pokemon) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ){
            Text(text = "Pokémon Information")
            Text(text = "Name: ${pokemon.name}")
            Text(text = "National Dex Number: ${pokemon.nationalDexNumber}")
        }
    }
}