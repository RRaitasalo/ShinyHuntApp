package com.example.shinyhuntapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.HuntMethod
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.viewmodels.HuntViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuntScreen(
    navController: NavController,
    pokemonId: Int,
    huntViewModel: HuntViewModel
) {
    val currentPokemon by huntViewModel.currentPokemon.collectAsState()
    val encounters by huntViewModel.encounters.collectAsState()
    val incrementAmount by huntViewModel.incrementAmount.collectAsState()
    val isCustomAmount by huntViewModel.isCustomAmount.collectAsState()
    val huntMethod by huntViewModel.huntMethod.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showDeleteHuntDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val huntSavedMessage = stringResource(R.string.hunt_saved)

    val currentRoute = Routes.huntWithPokemonId(pokemonId)
    val currentRouteForNav = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(pokemonId) {
        huntViewModel.startNewHunt(pokemonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.shiny_hunt),
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
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.hunt_settings)
                        )
                    }
                    IconButton(onClick = { showDeleteHuntDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_hunt)
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
                    selected = currentRouteForNav == Routes.POKEMON_LIST,
                    onClick = { navController.navigate(Routes.POKEMON_LIST) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.hunt)) },
                    label = { Text(stringResource(R.string.hunt)) },
                    selected = currentRouteForNav?.startsWith(Routes.HUNT) == true,
                    onClick = { navController.navigate(Routes.HUNT) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentRouteForNav == Routes.SETTINGS,
                    onClick = { navController.navigate(Routes.SETTINGS) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        currentPokemon?.let { pokemon ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
               // Pokemon info card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = pokemon.spriteUrl,
                            contentDescription = stringResource(R.string.picture_of, pokemon.name),
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = pokemon.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.card_dex_number, pokemon.nationalDexNumber),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Text(
                            text = stringResource(R.string.hunt_method, huntMethod.displayName),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Encounter card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.encounters),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = encounters.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Increment Amount Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.add),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(1, 2, 5, 15).forEach { amount ->
                                    FilterChip(
                                        onClick = {
                                            huntViewModel.setIncrementAmount(amount)
                                            huntViewModel.setIsCustomAmount(false)
                                        },
                                        label = { Text(amount.toString()) },
                                        selected = incrementAmount == amount && !isCustomAmount,
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                                FilterChip(
                                    onClick = { showCustomDialog = true },
                                    label = {
                                        Text(if (isCustomAmount) incrementAmount.toString() else stringResource(R.string.custom))
                                    },
                                    selected = isCustomAmount,
                                    modifier = Modifier.height(32.dp)
                                )
                            }
                        }
                        // Add Encounter Button
                        Button(
                            onClick = { huntViewModel.addEncounter() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pluralStringResource(
                                    R.plurals.add_encounters,
                                    incrementAmount,
                                    incrementAmount
                                ),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Save Hunt Button
                    OutlinedButton(
                        onClick = {
                            huntViewModel.saveHunt()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(huntSavedMessage)
                            }
                          },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.save_hunt))
                    }

                    // Complete Hunt Button
                    Button(
                        onClick = { showCompleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.complete_hunt))
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomAmountDialog(
            currentAmount = incrementAmount,
            onConfirm = { amount ->
                huntViewModel.setIncrementAmount(amount)
                huntViewModel.setIsCustomAmount(true)
                showCustomDialog = false
            },
            onDismiss = { showCustomDialog = false }
        )
    }

    if (showSettingsDialog) {
        HuntSettingsDialog(
            huntMethod = huntMethod,
            onHuntMethodChange = { huntViewModel.setHuntMethod(it) },
            onDismiss = { showSettingsDialog = false },
            onSave = {
                huntViewModel.saveHunt()
                showSettingsDialog = false
            }
        )
    }

    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text(stringResource(R.string.complete_hunt)) },
            text = {
                Text(stringResource(R.string.complete_hunt_message, currentPokemon?.name ?: stringResource(R.string.unknown)))
            },
            confirmButton = {
                Button(
                    onClick = {
                        huntViewModel.completeHunt {
                            showCompleteDialog = false
                            navController.navigate(Routes.MAIN) {
                                popUpTo(currentRoute) { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.complete_hunt))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteHuntDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteHuntDialog = false },
            title = { Text(stringResource(R.string.delete_hunt)) },
            text = { Text(stringResource(R.string.delete_hunt_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        huntViewModel.deleteHunt {
                            showDeleteHuntDialog = false
                            navController.navigate(Routes.MAIN) {
                                popUpTo(currentRoute) { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuntSettingsDialog(
    huntMethod: HuntMethod,
    onHuntMethodChange: (HuntMethod) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {

    val huntMethods = HuntMethod.entries.toList()
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hunt_settings)) },
        text = {
            Column {

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = huntMethod.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.hunt_method)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        huntMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.displayName) },
                                onClick = {
                                    onHuntMethodChange(method)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun CustomAmountDialog(
    currentAmount: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var textValue by remember { mutableStateOf(currentAmount.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.custom_increment_amount)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.enter_custom_amount),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        textValue = newValue
                        isError =
                            newValue.toIntOrNull() == null || (newValue.toIntOrNull() ?: 0) <= 0
                    },
                    label = { Text(stringResource(R.string.amount)) },
                    placeholder = { Text(stringResource(R.string.enter_number)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isError,
                    supportingText = if (isError) {
                        { Text(stringResource(R.string.please_enter_valid_number)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = textValue.toIntOrNull()
                    if (amount != null && amount > 0) {
                        onConfirm(amount)
                    }
                },
                enabled = !isError && textValue.isNotBlank()
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}