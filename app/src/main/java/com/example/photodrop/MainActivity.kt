package com.example.photodrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.photodrop.ui.foto.FotoAufnahmeScreen
import com.example.photodrop.ui.theme.PhotoDropTheme

// Einstiegspunkt der App — Android startet hier.
// Richtet das Design ein und zeigt den Hauptscreen.
class MainActivity : ComponentActivity() {

    // Wird einmal aufgerufen wenn die App startet.
    // Setzt die Compose-UI als Inhalt der Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoDropTheme {
                FotoAufnahmeScreen()
            }
        }
    }
}
