package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.local.AppDatabase
import com.example.shinyhuntapp.data.local.User
import kotlinx.coroutines.launch
import androidx.core.content.edit

class LoginViewModel(context: Context) : ViewModel() {
    private val db = AppDatabase.getDatabase(context)
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun login(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val user = db.userDao().getUserByUsername(username)
            if (user != null && user.password == password) {
                sharedPreferences.edit { putInt("logged_in_user_id", user.id) }
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun register(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val existingUser = db.userDao().getUserByUsername(username)
            if (existingUser == null) {
                val user = User(username = username, password = password)
                db.userDao().insertUser(user)
                val newUser = db.userDao().getUserByUsername(username)
                newUser?.let {
                    sharedPreferences.edit { putInt("logged_in_user_id", it.id) }
                    onSuccess()
                } ?: onFailure()
            } else {
                onFailure()
            }
        }
    }
}