package com.example.shinyhuntapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Hunt
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.components.HuntCard
import com.example.shinyhuntapp.ui.components.ShinyCard
import com.example.shinyhuntapp.viewmodels.HuntViewModel
import com.example.shinyhuntapp.viewmodels.LoginViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController, pokemonViewModel: PokemonViewModel, loginViewModel: LoginViewModel, huntViewModel: HuntViewModel) {

    val allHunts by huntViewModel.allHunts.collectAsState()
    val userPokemonMap by pokemonViewModel.userPokemonMap.collectAsState()
    val isUserPokemonLoaded by pokemonViewModel.isUserPokemonLoaded.collectAsState()

    val ongoingHunts = allHunts.filter { it.endDate == null }.take(3)
    val capturedShinies = userPokemonMap.filter { it.value.hasCaughtShiny }.map { it.value.pokemonId }

    LaunchedEffect(Unit) {
        pokemonViewModel.fetchAndStorePokemonIfNeeded()
        pokemonViewModel.fetchPokemonList()
        pokemonViewModel.fetchUserPokemon()
        huntViewModel.getAllHunts()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HuntSection(
                navController = navController,
                title = stringResource(R.string.ongoing_hunts),
                hunts = ongoingHunts
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShinySection(
                navController = navController,
                title = stringResource(R.string.captured_shinies),
                pokemonIds = capturedShinies,
                pokemonViewModel = pokemonViewModel,
                isLoaded = isUserPokemonLoaded
            )
        }
    }
}

@Composable
private fun HuntSection(
    navController: NavController,
    title: String,
    hunts: List<Hunt>
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        if (hunts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.no_hunts_yet),
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                hunts.forEach { hunt ->
                    HuntCard(
                        navController = navController,
                        hunt = hunt,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShinySection(
    navController: NavController,
    title: String,
    pokemonIds: List<Int>,
    pokemonViewModel: PokemonViewModel,
    isLoaded: Boolean
) {
    val pokemonList by pokemonViewModel.pokemonList.collectAsState()
    val pokemonMap = pokemonList.associateBy { it.id }
    val shinies = pokemonIds.mapNotNull { pokemonMap[it] }

    Column {
        Text(
            text = title,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        if (!isLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (shinies.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "No shinies caught yet",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(shinies) { pokemon ->
                    ShinyCard(
                        navController = navController,
                        pokemon = pokemon
                    )
                }
            }
        }
    }
}
