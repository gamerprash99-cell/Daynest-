package com.daynest.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.preferences.ThemeOption
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.DaynestSectionHeader
import com.daynest.ui.theme.DaynestSpacing

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    var name by remember(state.userName) { mutableStateOf(state.userName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DaynestSpacing.md)
    ) {
        DaynestSectionHeader(title = "Settings")
        Spacer(Modifier.height(DaynestSpacing.lg))

        Text("Your name", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(DaynestSpacing.xs))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.setUserName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(DaynestSpacing.lg))
        Text("Appearance", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(DaynestSpacing.xs))
        Row(horizontalArrangement = Arrangement.spacedBy(DaynestSpacing.xs)) {
            ThemeOption.entries.forEach { option ->
                FilterChip(
                    selected = state.themeOption == option,
                    onClick = { viewModel.setThemeOption(option) },
                    label = { Text(option.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }
    }
}
