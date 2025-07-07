package com.example.shinyhuntapp.data.local

import com.squareup.moshi.Json

data class PokemonListResponse(
    @Json(name = "results")
    val results: List<PokemonEntry>
)

data class PokemonEntry(
    val name: String,
    val url: String
)