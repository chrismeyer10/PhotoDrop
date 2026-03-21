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

@Composable
fun kameraAktionErstellen(onFotoGemacht: (Uri) -> Unit): () -> Unit {
    val kontext = LocalContext.current
    var aktuelleUri by remember { mutableStateOf<Uri?>(null) }

    val kameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { erfolgreich ->
        if (erfolgreich) aktuelleUri?.let(onFotoGemacht)
    }

    val erlaubnisLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { erteilt ->
        if (erteilt) {
            val uri = dateiUriErstellen(kontext)
            aktuelleUri = uri
            kameraLauncher.launch(uri)
        }
    }

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
