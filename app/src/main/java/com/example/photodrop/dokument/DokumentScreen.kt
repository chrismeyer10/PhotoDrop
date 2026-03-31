package com.example.photodrop.dokument

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.drive.DriveViewModel
import com.example.photodrop.ui.drive.zustand.DriveZustand
import com.example.photodrop.ui.foto.kamera.dateiUriErstellen
import com.example.photodrop.ui.foto.kamera.hatKameraErlaubnis

// Stateful: Verbindet DokumentViewModel mit UI, Kamera-Permission und Drive-Zustand.
@Composable
fun DokumentScreen(
    viewModel: DokumentViewModel = viewModel(),
    driveViewModel: DriveViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {},
    onEinstellungenOeffnen: () -> Unit = {}
) {
    val zustand by viewModel.zustand.collectAsState()
    val driveZustand by driveViewModel.zustand.collectAsState()
    val context = LocalContext.current

    var kameraUri by remember { mutableStateOf<Uri?>(null) }
    var zeigAnmeldeHinweis by remember { mutableStateOf(false) }

    val kameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { erfolg -> if (erfolg) kameraUri?.let { viewModel.dokumentSetzen(it, context) } }

    val kameraErlaubnisLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { erlaubt ->
        if (erlaubt) {
            val uri = dateiUriErstellen(context)
            kameraUri = uri
            kameraLauncher.launch(uri)
        }
    }

    val dateiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.dokumentSetzen(it, context) } }

    val token = driveTokenHolen(driveZustand)
    val ordnerId = driveOrdnerIdHolen(driveZustand)

    if (zeigAnmeldeHinweis) {
        AlertDialog(
            onDismissRequest = { zeigAnmeldeHinweis = false },
            title = { Text("Nicht bei Drive angemeldet") },
            text = { Text("Bitte melde dich zuerst bei Google Drive an, bevor du ein Dokument speicherst.") },
            confirmButton = { Button(onClick = { zeigAnmeldeHinweis = false }) { Text("OK") } }
        )
    }

    DokumentInhalt(
        zustand = zustand,
        onFotografieren = {
            if (hatKameraErlaubnis(context)) {
                val uri = dateiUriErstellen(context)
                kameraUri = uri
                kameraLauncher.launch(uri)
            } else {
                kameraErlaubnisLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onDateiAuswaehlen = {dateiLauncher.launch(arrayOf("*/*")) },
        onAnalysieren = { viewModel.analysieren() },
        onHochladen = { name, ordner ->
            if (token != null && ordnerId != null) {
                viewModel.hochladen(name, ordner, token, ordnerId)
            } else {
                zeigAnmeldeHinweis = true
            }
        },
        onTrotzdemSpeichern = { viewModel.trotzdemSpeichern() },
        onOcrAnalysieren = { viewModel.ocrAnalysieren() },
        onEinstellungenOeffnen = onEinstellungenOeffnen,
        onZuruecksetzen = { viewModel.zuruecksetzen() },
        onMenuOeffnen = onMenuOeffnen
    )
}

// Holt den Token aus dem Drive-Zustand (inkl. InhaltGeladen).
private fun driveTokenHolen(zustand: DriveZustand): String? {
    return when (zustand) {
        is DriveZustand.Verbunden -> zustand.token
        is DriveZustand.InhaltGeladen -> zustand.token
        else -> null
    }
}

// Holt die Ordner-ID aus dem Drive-Zustand.
private fun driveOrdnerIdHolen(zustand: DriveZustand): String? {
    return when (zustand) {
        is DriveZustand.Verbunden -> zustand.ordnerId
        is DriveZustand.InhaltGeladen -> zustand.ordnerId
        else -> null
    }
}
