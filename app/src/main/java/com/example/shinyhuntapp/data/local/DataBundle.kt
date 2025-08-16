package com.example.shinyhuntapp.data.local

data class DataBundle(
    val pokemon: List<Pokemon>,
    val version: String = "0.1",
    val lastUpdated: String
)