package com.example.shinyhuntapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shinyhuntapp.data.local.GameAvailability
import com.example.shinyhuntapp.data.local.Pokemon

data class PokemonWithGames (
    @Embedded val pokemon: Pokemon,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemonId",
        entity = GameAvailability::class
    )
    val gameAvailability: List<GameAvailabilityWithGame>
)