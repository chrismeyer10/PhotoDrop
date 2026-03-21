package com.example.photodrop.ui.foto

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

// Bereitet alles vor, damit ein Foto gemacht werden kann.
// Gibt eine einfache Funktion zurück — wenn man sie aufruft, öffnet sich die Kamera.
// Kümmert sich intern um: Erlaubnis anfragen, Datei anlegen, Kamera starten.
@Composable
fun kameraAktionErstellen(onFotoGemacht: (Uri) -> Unit): () -> Unit {
    val kontext = LocalContext.current

    // Speichert die URI der Datei, in die das aktuelle Foto gespeichert wird.
    var aktuelleUri by remember { mutableStateOf<Uri?>(null) }

    // Startet die Kamera-App. Wenn fertig: Foto-URI an den Aufrufer weitergeben.
    val kameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { erfolgreich ->
        if (erfolgreich) aktuelleUri?.let(onFotoGemacht)
    }

    // Fragt den Nutzer nach der Kamera-Erlaubnis.
    // Wenn er zustimmt: sofort Foto machen.
    val erlaubnisLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { erteilt ->
        if (erteilt) {
            val uri = dateiUriErstellen(kontext)
            aktuelleUri = uri
            kameraLauncher.launch(uri)
        }
    }

    // Die zurückgegebene Funktion: öffnet Kamera oder fragt zuerst nach Erlaubnis.
    return {
        if (hatKameraErlaubnis(kontext)) {
            val uri = dateiUriErstellen(kontext)
            aktuelleUri = uri
            kameraLauncher.launch(uri)
        } else {
            erlaubnisLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
