package com.daveleeds.arrowdemo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daveleeds.arrowdemo.Wrestler
import com.daveleeds.arrowdemo.viewmodel.WrestlerEditStatus
import com.daveleeds.arrowdemo.viewmodel.WrestlerEditUiState

@Composable
fun WrestlerEditScreen(
    uiState: WrestlerEditUiState,
    onSetName: (String) -> Unit,
    onSetAge: (Int) -> Unit,
    onSetWeight: (Int) -> Unit,
    onSetCity: (String) -> Unit,
    onSetCountry: (String) -> Unit,
    onSaved: (Wrestler) -> Unit,
    onBack: () -> Unit
) {
    if (uiState.status in listOf(WrestlerEditStatus.LOADING, WrestlerEditStatus.SAVING)) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        uiState.exception?.let { e -> Text("Error: ${e.message}", color = colorScheme.error) }

        Text(
            text = "Want to edit ${uiState.wrestler.name}?",
            style = typography.headlineLarge
        )

        TextField(
            value = uiState.wrestler.name,
            onValueChange = { name -> onSetName(name) },
            label = { Text("Name") }
        )
        TextField(
            value = uiState.wrestler.age.toString(),
            onValueChange = { age -> age.toIntOrNull()?.let { onSetAge(it) } },
            label = { Text("Age") }
        )
        TextField(
            value = uiState.wrestler.weight.toString(),
            onValueChange = { weight -> weight.toIntOrNull()?.let { onSetWeight(it) } },
            label = { Text("Weight") }
        )
        TextField(
            value = uiState.wrestler.hometown.city,
            onValueChange = { city -> onSetCity(city) },
            label = { Text("City") }
        )
        TextField(
            value = uiState.wrestler.hometown.country,
            onValueChange = { country -> onSetCountry(country) },
            label = { Text("Country") }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onBack() },
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Don't Save")
            }
            Button(
                onClick = { onSaved(uiState.wrestler) },
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Save")
            }
        }
    }
}

