package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.GuestUser
import com.example.shinyhuntapp.data.local.User
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {
    private val db = DatabaseProvider.getDatabase(context)
    private val preferences = PreferenceManager(context)
    private val userDao = db.userDao()

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

    fun logout() {
        preferences.clearLoggedInUserId()
    }

    suspend fun ensureGuestUserExists() {
        val guestUser = userDao.getUserById(GuestUser.ID)
        if (guestUser == null) {
            userDao.insertUser(
                User(
                    id = GuestUser.ID,
                    username = GuestUser.USERNAME,
                    password = ""
                )
            )
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                ensureGuestUserExists()
                preferences.saveLoggedInUserId(GuestUser.ID)
                onSuccess()
            } catch (e: Exception) {
                onFailure()
            }
        }
    }
}

class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(context) as T
    }
}