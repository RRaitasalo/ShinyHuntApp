package com.example.shinyhuntapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.viewmodels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, context: Context) {
    val viewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(context) as T
            }
        }
    )

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val wrongCredentialsMessage = stringResource(R.string.wrong_credentials)
    val errorLoggingInAsGuestMessage = stringResource(R.string.error_logging_in_as_guest)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.headlineMedium
                )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = CenterHorizontally
        ) {
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    viewModel.login(
                        username.value,
                        password.value,
                        onSuccess = { navController.navigate(Routes.MAIN) },
                        onFailure = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(wrongCredentialsMessage)
                            }
                        }
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.login))
            }
            Button(
                onClick = {
                    navController.navigate(Routes.REGISTER) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.create_account))
            }
            Button(
                onClick = {
                    viewModel.loginAsGuest(
                        onSuccess = {
                            navController.navigate(Routes.MAIN) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onFailure = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(errorLoggingInAsGuestMessage)
                            }
                        }
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.continue_as_guest))
            }
        }
    }
}