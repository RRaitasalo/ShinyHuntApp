package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.data.local.DatabaseProvider
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {
    private val db = DatabaseProvider.getDatabase(context)
    private val preferences = PreferenceManager(context)

    fun login(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val user = db.userDao().getUserByUsername(username)
            if (user != null && user.password == password) {
                preferences.saveLoggedInUserId(user.id)
                onSuccess()
            } else {
                onFailure()
            }
        }
    }
}