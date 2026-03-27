package com.example.photodrop.dokument

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

// Stateful: Verbindet DokumentViewModel mit UI und Activity-Result-Launchern.
@Composable
fun DokumentScreen(
    viewModel: DokumentViewModel = viewModel(),
    driveViewModel: DriveViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {}
) {
    val zustand by viewModel.zustand.collectAsState()
    val driveZustand by driveViewModel.zustand.collectAsState()
    val context = LocalContext.current

    var kameraUri by remember { mutableStateOf<Uri?>(null) }

    val kameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { erfolg -> if (erfolg) kameraUri?.let { viewModel.dokumentSetzen(it, context) } }

    val dateiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.dokumentSetzen(it, context) } }

    val token = driveTokenHolen(driveZustand)
    val ordnerId = driveOrdnerIdHolen(driveZustand)

    DokumentInhalt(
        zustand = zustand,
        onFotografieren = {
            val uri = dateiUriErstellen(context)
            kameraUri = uri
            kameraLauncher.launch(uri)
        },
        onDateiAuswaehlen = { dateiLauncher.launch("image/*") },
        onAnalysieren = { viewModel.analysieren() },
        onHochladen = { name, ordner ->
            if (token != null && ordnerId != null) {
                viewModel.hochladen(name, ordner, token, ordnerId)
            }
        },
        onTrotzdemSpeichern = { viewModel.trotzdemSpeichern() },
        onZuruecksetzen = { viewModel.zuruecksetzen() },
        onMenuOeffnen = onMenuOeffnen
    )
}

// Holt den Token aus dem Drive-Zustand.
private fun driveTokenHolen(zustand: DriveZustand): String? {
    return when (zustand) {
        is DriveZustand.Verbunden -> zustand.token
        is DriveZustand.InhaltGeladen -> null
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
