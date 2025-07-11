package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@Composable
fun DevToolsScreen(
    navController: NavController,
    viewModel: PokemonViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Developer Tools", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = {
            viewModel.clearPokemonTable()
        }) {
            Text("❌ Delete All Pokémon")
        }

        Button(onClick = {
            viewModel.resetFirstLaunchFlag()
        }) {
            Text("🔄 Reset Fetch Flag")
        }

        Button(onClick = {
            viewModel.forceFetchPokemon()
        }) {
            Text("⬇️ Fetch All Pokémon Now")
        }

        Button(onClick = {
            viewModel.getPokemon(1)
        }) {
            Text("Fetch One Pokemon (Should Fix App Inspection)")
        }
    }
}
