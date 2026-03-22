package com.example.photodrop.dokument

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

// Einzelner Eintrag im Archivierungs-Verlauf.
data class VerlaufEintrag(
    val beschreibung: String,
    val dateiname: String,
    val unterordner: String
)

// Speichert vergangene Archivierungs-Entscheidungen fuer KI-Lernkontext.
class DokumentVerlauf(private val prefs: SharedPreferences) {

    // Fuegt einen neuen Eintrag zum Verlauf hinzu.
    fun eintragHinzufuegen(eintrag: VerlaufEintrag) {
        val liste = letzteEintraege(MAX_EINTRAEGE - 1).toMutableList()
        liste.add(0, eintrag)
        val array = JSONArray()
        liste.forEach { array.put(eintragZuJson(it)) }
        prefs.edit().putString(SCHLUESSEL, array.toString()).apply()
    }

    // Gibt die letzten Eintraege zurueck.
    fun letzteEintraege(anzahl: Int = MAX_EINTRAEGE): List<VerlaufEintrag> {
        val json = prefs.getString(SCHLUESSEL, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            val ergebnis = mutableListOf<VerlaufEintrag>()
            for (i in 0 until minOf(array.length(), anzahl)) {
                ergebnis.add(jsonZuEintrag(array.getJSONObject(i)))
            }
            ergebnis
        } catch (_: Exception) {
            emptyList()
        }
    }

    // Wandelt einen Eintrag in ein JSON-Objekt.
    private fun eintragZuJson(eintrag: VerlaufEintrag): JSONObject {
        return JSONObject().apply {
            put("beschreibung", eintrag.beschreibung)
            put("dateiname", eintrag.dateiname)
            put("unterordner", eintrag.unterordner)
        }
    }

    // Liest einen Eintrag aus einem JSON-Objekt.
    private fun jsonZuEintrag(obj: JSONObject): VerlaufEintrag {
        return VerlaufEintrag(
            beschreibung = obj.optString("beschreibung", ""),
            dateiname = obj.optString("dateiname", ""),
            unterordner = obj.optString("unterordner", "")
        )
    }

    companion object {
        private const val SCHLUESSEL = "dokument_verlauf"
        private const val MAX_EINTRAEGE = 20
    }
}
