package com.example.shinyhuntapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
        pokemonViewModel.getPokemonByGame("Gold")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { navController.navigate(Routes.POKEMON_LIST) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.pokemon_list))
        }
        Button(
            onClick = { navController.navigate(Routes.DEV_TOOLS) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.dev_tools))
        }
        Button(
            onClick = {
                loginViewModel.logout()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.log_out))
        }
        Button(
            onClick = { navController.navigate(Routes.HUNT) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.hunt))
        }
    }
}
