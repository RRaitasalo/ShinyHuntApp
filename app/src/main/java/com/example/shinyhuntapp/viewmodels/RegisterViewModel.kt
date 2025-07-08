package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.User
import kotlinx.coroutines.launch

class RegisterViewModel(context: Context): ViewModel() {
    private val db = DatabaseProvider.getDatabase(context)
    private val preferences = PreferenceManager(context)

    fun register(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val existingUser = db.userDao().getUserByUsername(username)
            if (existingUser == null) {
                val user = User(username = username, password = password)
                db.userDao().insertUser(user)
                val newUser = db.userDao().getUserByUsername(username)
                newUser?.let {
                    preferences.saveLoggedInUserId(user.id)
                    onSuccess()
                } ?: onFailure()
            } else {
                onFailure()
            }
        }
    }
}