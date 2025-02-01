package com.daveleeds.arrowdemo.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daveleeds.arrowdemo.IMAGE_URL
import com.daveleeds.arrowdemo.Wrestler
import com.daveleeds.arrowdemo.viewmodel.WrestlerListStatus
import com.daveleeds.arrowdemo.viewmodel.WrestlerListUiState

@Composable
fun WrestlerList(
    uiState: WrestlerListUiState,
    onWrestlerChosen: (Int) -> Unit,
    onRetry: () -> Unit
) {
    Box(Modifier.padding(16.dp)) {
        when (uiState.status) {
            WrestlerListStatus.LOADING -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            WrestlerListStatus.ERROR -> {
                ErrorMessage(uiState.exception?.message ?: "Something went wrong....", onTryAgain = { onRetry() })
            }
            else -> {
                LazyColumn {
                    items(
                        count = uiState.wrestlers.size,
                        key = { index -> uiState.wrestlers[index].id }
                    ) { index ->
                        WrestlerRow(
                            wrestler = uiState.wrestlers[index],
                            onClick = { onWrestlerChosen(uiState.wrestlers[index].id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String?, onTryAgain: () -> Unit) {
    Row {
        Text("Error: $message", color = colorScheme.error)
        Spacer(Modifier.width(8.dp))
        Text(
            "Try again.",
            textDecoration = TextDecoration.Underline,
            color = colorScheme.error,
            modifier = Modifier.clickable { onTryAgain() }
        )
    }
}

@Composable
private fun WrestlerRow(wrestler: Wrestler, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp).clickable { onClick() }) {
        AsyncImage(
            model = "$IMAGE_URL/${wrestler.id}.png",
            contentDescription = "${wrestler.name} profile picture",
            modifier = Modifier.size(128.dp)
        )
        Column(modifier = Modifier.weight(1.0f)) {
            Text(wrestler.name, style = typography.headlineLarge)
            Text("Age: ${wrestler.age} years", style = typography.bodyLarge)
            Text("Weight: ${wrestler.weight} lbs", style = typography.bodyLarge)
            Text("Hometown: ${wrestler.hometown.city}, ${wrestler.hometown.country}", style = typography.bodyLarge)
        }
    }
}
