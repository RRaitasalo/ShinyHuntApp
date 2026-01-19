package com.example.shinyhuntapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.GameMasterData
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.viewmodels.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(navController: NavController, viewModel: PokemonViewModel) {

    val pokemonList by viewModel.pokemonList.collectAsState()
    val userPokemonMap by viewModel.userPokemonMap.collectAsState()
    var isFilterMenuVisible by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<String?>(null) }
    var selectedGenerations by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showOnlyShinyDex by remember { mutableStateOf(false) }
    val filterLength = 240

    val filteredPokemonList by remember {
        derivedStateOf {
            if (showOnlyShinyDex) {
                pokemonList.filter { pokemon ->
                    userPokemonMap[pokemon.id]?.hasCaughtShiny == true
                }
            } else {
                pokemonList
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(Unit) {
        viewModel.fetchPokemonList()
        viewModel.fetchUserPokemon()
    }

    LaunchedEffect(selectedGame, selectedGenerations) {
        when {
            selectedGame != null && selectedGenerations.isNotEmpty() -> {
                viewModel.getPokemonByGameAndGenerations(selectedGame!!, selectedGenerations)
            }
            selectedGame != null -> {
                viewModel.getPokemonByGame(selectedGame!!)
            }
            selectedGame == null && selectedGenerations.isNotEmpty() -> {
                viewModel.getPokemonByGenerations(selectedGenerations)
            }
            else -> {
                viewModel.fetchPokemonList()
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.pokemon_list),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        var selectedIndex by remember { mutableIntStateOf(0) }
                        val options = listOf(stringResource(R.string.all), stringResource(R.string.shinydex))

                        SingleChoiceSegmentedButtonRow {
                            options.forEachIndexed { index, option ->
                                SegmentedButton(
                                    onClick = {
                                        selectedIndex = index
                                        showOnlyShinyDex = (index == 1)
                                    },
                                    selected = selectedIndex == index,
                                    label = { Text(option) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    )
                                )
                            }
                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFilterMenuVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.filter_pokemon)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(120.dp)
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(filteredPokemonList) { pokemon ->
                PokemonCard(
                    navController = navController,
                    pokemon = pokemon,
                    onShinyToggle = { pokemon -> viewModel.toggleShinyStatus(pokemon.id)},
                    hasCaughtShiny = userPokemonMap[pokemon.id]?.hasCaughtShiny == true,
                    hasCaughtShinyWithHunt = userPokemonMap[pokemon.id]?.caughtDate != null
                )
            }
        }
    }

    AnimatedVisibility(
        visible = isFilterMenuVisible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(filterLength)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(filterLength)
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FilterMenu(
                selectedGame = selectedGame,
                selectedGenerations = selectedGenerations,
                onGameSelectionChange = { selectedGame = it },
                onGenerationsSelectionChange = { selectedGenerations = it },
                onDismiss = { isFilterMenuVisible = false },
                onClearFilters = {
                    selectedGame = null
                    selectedGenerations = emptySet()
                },
                filterLength = filterLength,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun FilterMenu(
    selectedGame: String?,
    selectedGenerations: Set<Int>,
    onGameSelectionChange: (String?) -> Unit,
    onGenerationsSelectionChange: (Set<Int>) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit,
    filterLength: Int,
    modifier: Modifier = Modifier
) {
    var isGenerationsExpanded by remember { mutableStateOf(false) }
    var isGamesExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(filterLength.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_pokemon),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_filter_menu)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Generations Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isGenerationsExpanded = !isGenerationsExpanded },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.generations),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = if (isGenerationsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isGenerationsExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                        )
                    }
                }

                AnimatedVisibility(visible = isGenerationsExpanded) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            (1..9).forEach { generation ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val newSelection = if (selectedGenerations.contains(generation)) {
                                                selectedGenerations - generation
                                            } else {
                                                selectedGenerations + generation
                                            }
                                            onGenerationsSelectionChange(newSelection)
                                        }
                                ) {
                                    Checkbox(
                                        checked = selectedGenerations.contains(generation),
                                        onCheckedChange = { isChecked ->
                                            val newSelection = if (isChecked) {
                                                selectedGenerations + generation
                                            } else {
                                                selectedGenerations - generation
                                            }
                                            onGenerationsSelectionChange(newSelection)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = stringResource(R.string.generation_x, generation),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Games Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isGamesExpanded = !isGamesExpanded },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.games),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            selectedGame?.let { game ->
                                Text(
                                    text = game,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isGamesExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isGamesExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                        )
                    }
                }

                AnimatedVisibility(visible = isGamesExpanded) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val gamesByGeneration = GameMasterData.GAMES.groupBy { it.generation ?: 0 }.toSortedMap()

                            gamesByGeneration.forEach { (generation, games) ->
                                if (generation > 0) {
                                    Text(
                                        text = stringResource(R.string.generation_x, generation),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )

                                    games.forEach { game ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onGameSelectionChange(
                                                        if (selectedGame == game.name) null else game.name
                                                    )
                                                }
                                        ) {
                                            Checkbox(
                                                checked = selectedGame == game.name,
                                                onCheckedChange = { isChecked ->
                                                    onGameSelectionChange(
                                                        if (isChecked) game.name else null
                                                    )
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = game.name,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.clear))
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.apply))
                }
            }
        }
    }
}

@Composable
fun PokemonCard(
    navController: NavController,
    pokemon: Pokemon,
    onShinyToggle: (Pokemon) -> Unit,
    hasCaughtShiny: Boolean,
    hasCaughtShinyWithHunt: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            navController.navigate(Routes.pokemonInfoWithId(pokemon.id))
        }
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                AsyncImage(
                    model = if (hasCaughtShiny) pokemon.shinySprite else pokemon.spriteUrl,
                    contentDescription = stringResource(R.string.picture_of, pokemon.name),
                    modifier = Modifier.size(96.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(
                            R.string.card_dex_number,
                            pokemon.nationalDexNumber.toString()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (!hasCaughtShinyWithHunt) {
                IconButton(
                    onClick = { onShinyToggle(pokemon) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (hasCaughtShiny) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (hasCaughtShiny) stringResource(R.string.shiny_caught) else stringResource(R.string.mark_as_shiny_caught),
                        tint = if (hasCaughtShiny) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}