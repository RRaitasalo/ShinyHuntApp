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
    @Json(name = "other") val other: OtherSprites,
)

data class OtherSprites(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtwork
)

data class OfficialArtwork(
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

enum class HuntMethod(val displayName: String) {
    RANDOM_ENCOUNTER("Random Encounter"),
    MASUDA_METHOD("Masuda Method"),
    RADAR("Radar"),
    SOFT_RESET("Soft Reset"),
    CHAIN_FISHING("Chain Fishing"),
    DEXNAV("DexNav"),
    SOS_CALLING("SOS Calling"),
    DYNAMAX_ADVENTURE("Dynamax Adventure"),
    OUTBREAK("Outbreak"),
    SANDWICH_METHOD("Sandwich Method");

    companion object {
        fun fromDisplayName(displayName: String): HuntMethod? {
            return HuntMethod.entries.find { it.displayName == displayName }
        }
    }
}