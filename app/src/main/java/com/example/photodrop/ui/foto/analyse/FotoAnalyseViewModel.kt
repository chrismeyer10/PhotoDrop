package com.example.photodrop.ui.foto.analyse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photodrop.BuildConfig
import com.example.photodrop.agent.AgentResult
import com.example.photodrop.agent.AgentService
import com.example.photodrop.skills.SkillRegistry
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Steuert die KI-Analyse eines Fotos ueber den AgentService.
class FotoAnalyseViewModel : ViewModel() {

    private val _zustand = MutableStateFlow<FotoAnalyseZustand>(FotoAnalyseZustand.Bereit)
    val zustand: StateFlow<FotoAnalyseZustand> = _zustand.asStateFlow()

    private val agentService = AgentService(BuildConfig.ANTHROPIC_API_KEY)

    // Aktueller Analyse-Job, damit er bei Abbruch gecancelt werden kann.
    private var analyseJob: Job? = null

    // Startet die KI-Analyse fuer das gegebene Foto.
    fun analysieren(uri: Uri) {
        _zustand.value = FotoAnalyseZustand.Laeuft
        analyseJob = viewModelScope.launch {
            val ergebnis = agentService.run(
                prompt = "Beschreibe dieses Foto kurz auf Deutsch: $uri",
                skills = SkillRegistry.all,
                systemPrompt = "Du bist ein Foto-Assistent. Beschreibe Fotos kurz und praegnant auf Deutsch."
            )
            _zustand.value = when (ergebnis) {
                is AgentResult.Success -> FotoAnalyseZustand.Ergebnis(ergebnis.text)
                is AgentResult.Error -> FotoAnalyseZustand.Fehler(ergebnis.message)
            }
        }
    }

    // Setzt den Zustand zurueck auf Bereit und bricht laufende Analyse ab.
    fun zuruecksetzen() {
        analyseJob?.cancel()
        analyseJob = null
        _zustand.value = FotoAnalyseZustand.Bereit
    }
}
