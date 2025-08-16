package com.example.shinyhuntapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.GameAvailability

data class PokemonWithGames (
    @Embedded val pokemon: Pokemon,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemonId",
        entity = GameAvailability::class
    )
    val gameAvailability: List<GameAvailabilityWithGame>
)