package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Hält die Liste aller aufgenommenen Fotos im Speicher.
// Wenn ein neues Foto gemacht wird, wird es hier hinzugefügt.
// Der Screen beobachtet diese Liste und aktualisiert sich automatisch.
class FotoViewModel : ViewModel() {

    // Interne, veränderbare Liste. Nur dieser ViewModel darf sie ändern.
    private val _fotos = MutableStateFlow<List<Uri>>(emptyList())

    // Öffentliche, schreibgeschützte Liste für den Screen.
    val fotos: StateFlow<List<Uri>> = _fotos.asStateFlow()

    // Fügt ein neues Foto an das Ende der Liste an.
    // uri = Adresse der Bilddatei auf dem Gerät.
    fun fotoHinzufuegen(uri: Uri) {
        _fotos.update { aktuelleList -> aktuelleList + uri }
    }
}
