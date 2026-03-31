package com.example.photodrop.ui.einstellungen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// URL zur Anthropic-Konsole fuer API-Key-Erstellung.
private const val CONSOLE_URL = "https://console.anthropic.com/account/keys"

// Dialog der erklaert dass Claude Pro kein API-Zugang gibt.
// Zeigt einen Link zur Anthropic-Konsole und eine Schliessen-Option.
@Composable
fun ClaudeApiHinweisDialog(onSchliessen: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onSchliessen,
        title = {
            Text(
                text = "Claude API-Key benoetigt",
                color = TextHell
            )
        },
        text = {
            ClaudeApiHinweisText()
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CONSOLE_URL))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zu console.anthropic.com ->", color = TextHell)
            }
        },
        dismissButton = {
            TextButton(onClick = onSchliessen) {
                Text("Schliessen", color = TextGedaempft)
            }
        }
    )
}

// Erklaerungstext fuer den Claude-API-Hinweis-Dialog.
@Composable
private fun ClaudeApiHinweisText() {
    Column {
        Text(
            text = "Ein Claude Pro oder Max Abo (claude.ai) gibt keinen API-Zugang — " +
                "das sind zwei getrennte Produkte von Anthropic.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextHell
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Fuer die API-Nutzung benoenigst du einen separaten Account:",
            style = MaterialTheme.typography.labelMedium,
            color = TextGedaempft
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "1. Konto unter console.anthropic.com anlegen\n" +
                "2. Zahlungsmethode hinterlegen\n" +
                "3. API-Key generieren und hier einfuegen",
            style = MaterialTheme.typography.bodySmall,
            color = TextGedaempft
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Claude API Hinweis")
@Composable
private fun ClaudeApiHinweisDialogVorschau() {
    PhotoDropTheme {
        ClaudeApiHinweisDialog(onSchliessen = {})
    }
}
