package com.example.shinyhuntapp.data.local

import com.squareup.moshi.Json

data class PokemonEntry(
    val name: String,
    val url: String
)

data class PokemonListResponse(
    @Json(name = "results")
    val results: List<PokemonEntry>
)

data class PokemonDetails(
    @Json(name = "name")
    val name: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "types")
    val types: List<TypeSlot>,
    @Json(name = "sprites")
    val sprites: Sprites
)

data class Sprites(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?
)


data class TypeSlot(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String,
    val url: String
)