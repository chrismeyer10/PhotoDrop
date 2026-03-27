package com.example.photodrop.dokument

import android.graphics.Bitmap
import android.net.Uri
import com.example.photodrop.agent.AgentResult
import org.json.JSONObject

// Verarbeitet die KI-Antwort und gibt den passenden Zustand zurueck.
fun ergebnisVerarbeiten(ergebnis: AgentResult, uri: Uri, vorschau: Bitmap?): DokumentZustand {
    return when (ergebnis) {
        is AgentResult.Error -> DokumentZustand.AnalyseFehler(
            meldung = fehlerUebersetzen(ergebnis.message),
            uri = uri,
            vorschau = vorschau
        )
        is AgentResult.Success -> jsonAntwortParsieren(ergebnis.text, uri, vorschau)
    }
}

// Parsed die JSON-Antwort der KI in einen VorschlagBereit-Zustand.
private fun jsonAntwortParsieren(text: String, uri: Uri, vorschau: Bitmap?): DokumentZustand {
    return try {
        val json = JSONObject(text.trim())
        DokumentZustand.VorschlagBereit(
            uri = uri,
            vorschau = vorschau,
            dateiname = json.getString("dateiname"),
            unterordner = json.getString("unterordner"),
            begruendung = json.optString("begruendung", "")
        )
    } catch (_: Exception) {
        DokumentZustand.Fehler("KI-Antwort konnte nicht verarbeitet werden")
    }
}
