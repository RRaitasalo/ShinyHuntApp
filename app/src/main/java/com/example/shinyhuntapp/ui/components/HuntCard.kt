package com.example.shinyhuntapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shinyhuntapp.R
import com.example.shinyhuntapp.data.local.Hunt
import com.example.shinyhuntapp.navigation.Routes
import com.example.shinyhuntapp.ui.screens.toDateString

@Composable
fun HuntCard(
    navController: NavController,
    hunt: Hunt,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier.size(24.dp).align(Alignment.End),
                contentAlignment = Alignment.Center
            ) {
                IconButton( onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit)) },
                        onClick = { navController.navigate(Routes.huntWithPokemonId(hunt.pokemon.id)) }
                    )
                }
            }
            AsyncImage(
                model = if (hunt.isFoundShiny) hunt.pokemon.shinySprite else hunt.pokemon.spriteUrl,
                contentDescription = stringResource(R.string.picture_of, hunt.pokemon.name),
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = hunt.pokemon.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.encounters),
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = hunt.encounters.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hunt.isFoundShiny) stringResource(R.string.completed) else stringResource(R.string.ongoing),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (hunt.isFoundShiny) {
                        Color(0xFF4CAF50)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (hunt.isFoundShiny) {
                    "${hunt.endDate?.toDateString()}"
                } else {
                    "${stringResource(R.string.started)} \n${hunt.startDate.toDateString()}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}