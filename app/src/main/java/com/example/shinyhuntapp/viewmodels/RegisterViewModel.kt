package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.local.AppDatabase
import com.example.shinyhuntapp.data.local.User
import kotlinx.coroutines.launch

class RegisterViewModel(context: Context): ViewModel() {
    private val db = AppDatabase.getDatabase(context)
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

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