package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Hunt
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.components.HuntCard
import com.example.shinyhuntapp.viewmodels.HuntViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHuntScreen(
    navController: NavController,
    huntViewModel: HuntViewModel,
    pokemonViewModel: PokemonViewModel
) {
    val pokemonList by pokemonViewModel.pokemonList.collectAsState()
    val allHunts by huntViewModel.allHunts.collectAsState()
    val recentHunts = allHunts.filter { it.endDate != null }.sortedByDescending { it.endDate }
    val ongoingHunts = allHunts.filter { it.endDate == null }.sortedBy { it.startDate }
    val huntsShown = 3

    var showPokemonSelector by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        pokemonViewModel.fetchPokemonList()
        huntViewModel.getAllHunts()
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.your_hunts),
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
                },
            )
        },
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
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HuntSection(
                navController = navController,
                title = stringResource(R.string.recent_hunts),
                hunts = recentHunts.take(huntsShown)
            )
            HuntSection(
                navController = navController,
                title = stringResource(R.string.ongoing_hunts),
                hunts = ongoingHunts.take(huntsShown)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { showPokemonSelector = true },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(stringResource(R.string.start_new_hunt))
                }
            }
        }
    }
    if (showPokemonSelector) {
        PokemonSelectorDialog(
            onPokemonSelected = { pokemon ->
                huntViewModel.startNewHunt(pokemon.id)
                showPokemonSelector = false
                navController.navigate(Routes.huntWithPokemonId(pokemon.id))
            },
            onDismiss = { showPokemonSelector = false },
            pokemonList = pokemonList
        )
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
private fun PokemonSelectorDialog(
    onPokemonSelected: (Pokemon) -> Unit,
    onDismiss: () -> Unit,
    pokemonList: List<Pokemon>
) {
    var searchQuery by remember { mutableStateOf("") }
    val allPokemon = pokemonList

    val filteredPokemon = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allPokemon
        } else {
            allPokemon.filter { pokemon ->
                pokemon.name.contains(searchQuery, ignoreCase = true) ||
                        pokemon.nationalDexNumber.toString().contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.select_pokemon))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search_pokemon)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPokemon) { pokemon ->
                    PokemonListItem(
                        pokemon = pokemon,
                        onClick = { onPokemonSelected(pokemon) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PokemonListItem(
    pokemon: Pokemon,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Card(
                modifier = Modifier.size(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.pokemon_id_format, pokemon.id),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(pokemon.name)
        }
    }
}

fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}