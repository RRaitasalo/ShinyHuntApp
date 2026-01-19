package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.components.HuntCard
import com.example.shinyhuntapp.viewmodels.HuntViewModel
import com.example.shinyhuntapp.viewmodels.PokemonViewModel


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

    val currentRouteForNav = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(x = (-28).dp).fillMaxWidth()
                    ) {
                        Text(
                            text = pokemon?.name?.replaceFirstChar { it.uppercase() } ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "#${pokemon?.nationalDexNumber?.toString()?.padStart(3, '0') ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
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
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = currentRouteForNav == Routes.MAIN,
                    onClick = { navController.navigate(Routes.MAIN) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.pokemon_list)) },
                    label = { Text(stringResource(R.string.pokedex)) },
                    selected = true,
                    onClick = { navController.navigate(Routes.POKEMON_LIST) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.hunt)) },
                    label = { Text(stringResource(R.string.hunt)) },
                    selected = currentRouteForNav?.startsWith(Routes.HUNT) == true,
                    onClick = { navController.navigate(Routes.HUNT) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ){
            // Sprites section
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = pokemon?.spriteUrl,
                    contentDescription = "Normal ${pokemon?.name}",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                VerticalDivider(modifier = Modifier.padding(horizontal = 4.dp))
                AsyncImage(
                    model = pokemon?.shinySprite,
                    contentDescription = "Shiny ${pokemon?.name}",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }
            
            if (hunt != null) {
                Text(text = stringResource(R.string.hunt_information))
                HuntCard(
                    navController = navController,
                    hunt = hunt!!,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = { navController.navigate(Routes.huntWithPokemonId(pokemonId)) }
                ) {
                    Text(text = if (hunt?.isFoundShiny == true) stringResource(R.string.start_new_hunt) else stringResource(R.string.continue_hunt))
                }
            } else {
                Button(
                    onClick = {
                        navController.navigate(Routes.huntWithPokemonId(pokemonId))
                    }
                ) {
                    Text(text = stringResource(R.string.start_new_hunt))
                }
            }
        }
    }
}