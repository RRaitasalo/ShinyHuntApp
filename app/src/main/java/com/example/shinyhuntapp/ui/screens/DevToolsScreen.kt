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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.viewmodels.LoginViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@Composable
fun DevToolsScreen(
    navController: NavController,
    pokemonViewModel: PokemonViewModel,
    loginViewModel: LoginViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.dev_tools), style = MaterialTheme.typography.headlineMedium)

        Button(onClick = {
            pokemonViewModel.clearPokemonTable()
        }) {
            Text(stringResource(R.string.delete_pokemon))
        }

        Button(onClick = {
            pokemonViewModel.resetFirstLaunchFlag()
        }) {
            Text(stringResource(R.string.reset_fetch_flag))
        }

        Button(onClick = {
            pokemonViewModel.forceFetchPokemon()
        }) {
            Text(stringResource(R.string.fetch_all_pokemon_now))
        }

        Button(onClick = {
            pokemonViewModel.getPokemon(1)
        }) {
            Text(stringResource(R.string.fetch_one_pokemon))
        }

        Button(
            onClick = {
                pokemonViewModel.clearPokemonTable()
                loginViewModel.logout()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
            }
        ) {
            Text(stringResource(R.string.log_out))
        }
    }
}
