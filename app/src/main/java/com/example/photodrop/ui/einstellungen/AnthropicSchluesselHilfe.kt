package com.example.photodrop.ui.einstellungen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// URL zur Anthropic-Konsole fuer die API-Key-Erstellung.
private const val ANTHROPIC_CONSOLE_URL = "https://console.anthropic.com/account/keys"

// Zeigt einen Button zum Oeffnen der Anthropic-Konsole und eine Kurzanleitung.
@Composable
fun AnthropicSchluesselHilfe(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Text(
            text = "Anleitung:",
            style = MaterialTheme.typography.labelMedium,
            color = TextGedaempft
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "1. Im Browser einloggen\n" +
                "2. 'Create Key' klicken\n" +
                "3. Key kopieren und unten einfuegen",
            style = MaterialTheme.typography.bodySmall,
            color = TextGedaempft
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { browserOeffnen(context, ANTHROPIC_CONSOLE_URL) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Text(
                text = "API-Key von console.anthropic.com holen ->",
                color = TextHell
            )
        }
    }
}

// Oeffnet eine URL im Standard-Browser des Geraets.
private fun browserOeffnen(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Anthropic Hilfe")
@Composable
private fun AnthropicSchluesselHilfeVorschau() {
    PhotoDropTheme {
        AnthropicSchluesselHilfe()
    }
}
