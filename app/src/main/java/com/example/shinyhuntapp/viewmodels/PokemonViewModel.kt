package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.PokemonDao
import com.example.shinyhuntapp.data.local.PokemonEntry
import com.example.shinyhuntapp.data.local.UserPokemonDao
import com.example.shinyhuntapp.data.network.PokeApiService
import com.example.shinyhuntapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val pokemonDao: PokemonDao,
    private val userPokemonDao: UserPokemonDao,
    private val api: PokeApiService
): ViewModel() {

    private val _pokemonList = MutableStateFlow<List<PokemonEntry>>(emptyList())
    val pokemonList: StateFlow<List<PokemonEntry>> = _pokemonList

    fun fetchPokemon() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPokemonList()
                _pokemonList.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class PokemonViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(context)
        val pokemonDao = db.pokemonDao()
        val userPokemonDao = db.userPokemonDao()
        val api = RetrofitInstance.api

        return PokemonViewModel(pokemonDao, userPokemonDao, api) as T
    }
}