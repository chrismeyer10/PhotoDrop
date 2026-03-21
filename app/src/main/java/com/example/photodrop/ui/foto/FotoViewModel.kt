package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FotoViewModel : ViewModel() {

    private val _fotos = MutableStateFlow<List<Uri>>(emptyList())
    val fotos: StateFlow<List<Uri>> = _fotos.asStateFlow()

    fun fotoHinzufuegen(uri: Uri) {
        _fotos.update { aktuelleList -> aktuelleList + uri }
    }
}
