package com.example.shinyhuntapp.data.local

data class DataBundle(
    val pokemon: List<Pokemon>,
    val games: List<Game>,
    val gameAvailability: List<GameAvailability>,
    val version: String = "0.2",
    val lastUpdated: String
)