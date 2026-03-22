package com.example.photodrop.skills

import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonPropertyDescription

// Gibt Basis-Informationen zu einem Foto anhand seiner URI zurueck.
@JsonClassDescription("Gibt Informationen zu einem Foto anhand seiner URI zurueck")
class GetPhotoInfoSkill : Skill {

    @JsonPropertyDescription("Die URI oder der Dateiname des Fotos")
    @JvmField
    var photoUri: String = ""

    // Liefert die verfuegbaren Foto-Informationen als JSON.
    override fun get(): String {
        return """{"uri": "$photoUri", "typ": "foto", "format": "jpg"}"""
    }
}
