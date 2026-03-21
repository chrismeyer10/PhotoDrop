package com.example.photodrop.skills

import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonPropertyDescription

/**
 * Beispiel-Skill: Gibt Basis-Informationen zu einem Foto zurück.
 * Ersetze die get()-Implementierung mit echter Logik.
 */
@JsonClassDescription("Gibt Informationen zu einem Foto anhand seiner URI zurück")
class GetPhotoInfoSkill : Skill {

    @JsonPropertyDescription("Die URI oder der Dateiname des Fotos")
    @JvmField
    var photoUri: String = ""

    override fun get(): String {
        // TODO: Echte Implementierung — z.B. ContentResolver, ExifInterface, etc.
        return """{"uri": "$photoUri", "available": true}"""
    }
}
