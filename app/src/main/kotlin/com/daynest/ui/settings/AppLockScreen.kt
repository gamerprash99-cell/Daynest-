package com.daynest.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import com.daynest.ui.components.PremiumChip
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumTextField
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockScreen() {
    val spacing = LocalDaynestSpacing.current
    var appLockEnabled by remember { mutableStateOf(false) }
    var pinEntry by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var pinSet by remember { mutableStateOf(false) }
    var showingPinEntry by remember { mutableStateOf(false) }
    var biometricAvailable by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        PremiumSectionHeader(
            title = "App Lock",
            subtitle = "Protect your private memories and data"
        )
        Spacer(modifier = Modifier.height(spacing.md))

        // App lock toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = appLockEnabled,
                onCheckedChange = { appLockEnabled = it },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = NestLavender,
                    checkedTrackColor = NestLavender.copy(alpha = 0.3f)
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Require authentication to open Daynest",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "PIN or biometric unlock",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        if (appLockEnabled) {
            // PIN setup or entry
            if (!pinSet && !showingPinEntry) {
                // Setup PIN
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set your PIN",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.md))
                    PremiumTextField(
                        value = pinEntry,
                        onValueChange = { pinEntry = it },
                        label = { Text("Enter 4-digit PIN") },
                        singleLine = true,
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(
                        text = "${pinEntry.length}/4",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.lg))
                    androidx.compose.material3.Button(
                        onClick = {
                            if (pinEntry.length == 4) {
                                pinConfirm = pinEntry
                                pinEntry = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Continue")
                    }
                }
            } else if (pinConfirm.isNotEmpty() && pinEntry.isEmpty()) {
                // Confirm PIN
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Confirm your PIN",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.md))
                    PremiumTextField(
                        value = pinEntry,
                        onValueChange = { pinEntry = it },
                        label = { Text("Re-enter 4-digit PIN") },
                        singleLine = true,
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(
                        text = "${pinEntry.length}/4",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.lg))
                    androidx.compose.material3.Button(
                        onClick = {
                            if (pinEntry == pinConfirm) {
                                pinSet = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Confirm PIN")
                    }
                }
            } else {
                // PIN is set - show options
                Column {
                    Text(
                        text = "PIN is set",
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.daynest.ui.theme.NestSageGreen
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    androidx.compose.material3.TextButton(
                        onClick = { /* change PIN */ },
                    ) {
                        Text(text = "Change PIN")
                    }
                    Spacer(modifier = Modifier.height(spacing.sm))
                    androidx.compose.material3.TextButton(
                        onClick = { /* remove PIN / disable lock */ appLockEnabled = false },
                    ) {
                        Text(text = "Remove PIN")
                    }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Biometric option
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = biometricAvailable,
                    onCheckedChange = { biometricAvailable = it },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = NestLavender,
                        checkedTrackColor = NestLavender.copy(alpha = 0.3f)
                    )
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Use biometric unlock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Fingerprint or face unlock",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Security info
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(),
            shape = com.daynest.ui.theme.DaynestShapes.medium,
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f)
            ),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(spacing.md)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security",
                    tint = com.daynest.ui.theme.NestSageGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = "Your PIN is securely stored on your device only.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nothing is uploaded to the cloud.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}

}
