package com.example.shinyhuntapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shinyhuntapp.data.local.Game
import com.example.shinyhuntapp.data.local.GameAvailability

data class GameAvailabilityWithGame(
    @Embedded val availability: GameAvailability,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "id"
    )
    val game: Game
)
