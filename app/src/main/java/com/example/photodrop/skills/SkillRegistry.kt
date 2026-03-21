package com.example.photodrop.skills

import java.util.function.Supplier

/**
 * Zentrale Liste aller verfügbaren Skills.
 * Neue Skills hier eintragen, damit sie im AgentService verfügbar sind.
 */
object SkillRegistry {
    val all: List<Class<out Supplier<String>>> = listOf(
        GetPhotoInfoSkill::class.java,
        // Weitere Skills hier hinzufügen:
        // UploadPhotoSkill::class.java,
        // DeletePhotoSkill::class.java,
    )
}
