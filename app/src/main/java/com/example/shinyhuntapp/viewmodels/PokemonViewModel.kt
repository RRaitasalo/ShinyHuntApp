package com.example.shinyhuntapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.shinyhuntapp.data.local.PokemonEntry
import com.example.shinyhuntapp.data.network.RetrofitInstance

class PokemonViewModel(context: Context): ViewModel() {

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