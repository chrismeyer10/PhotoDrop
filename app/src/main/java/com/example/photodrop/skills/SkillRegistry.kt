package com.example.photodrop.skills

import java.util.function.Supplier

// Zentrale Liste aller verfuegbaren Skills.
// Neue Skills hier eintragen, damit sie im AgentService verfuegbar sind.
object SkillRegistry {
    val all: List<Class<out Supplier<String>>> = emptyList()
}
