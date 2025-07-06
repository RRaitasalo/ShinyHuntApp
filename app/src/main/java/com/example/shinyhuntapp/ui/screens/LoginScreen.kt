package com.example.shinyhuntapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shinyhuntapp.viewmodels.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, context: Context) {
    val viewModel = LoginViewModel(context)
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = {
                viewModel.login(
                    username.value,
                    password.value,
                    onSuccess = { navController.navigate("main") },
                    onFailure = { /* TODO: Show error message */ }
                )
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Login")
        }
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Register")
        }
    }
}