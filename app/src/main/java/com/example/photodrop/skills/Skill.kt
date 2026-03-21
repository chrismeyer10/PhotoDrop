package com.example.photodrop.skills

import java.util.function.Supplier

/**
 * A Skill is a tool that Claude can call during an agent run.
 *
 * To create a new skill:
 * 1. Create a class that implements Skill (= Supplier<String>)
 * 2. Annotate the class with @JsonClassDescription("Was dieser Skill tut")
 * 3. Add public fields for each parameter, annotated with @JsonPropertyDescription
 * 4. Use @JvmField so Jackson can read the fields directly
 * 5. Register it in SkillRegistry
 *
 * Example:
 * ```kotlin
 * @JsonClassDescription("Ruft Metadaten eines Fotos ab")
 * class GetPhotoMetadataSkill : Skill {
 *     @JsonPropertyDescription("URI des Fotos")
 *     @JvmField var photoUri: String = ""
 *
 *     override fun get(): String {
 *         // Implementierung hier
 *         return """{"width": 1920, "height": 1080}"""
 *     }
 * }
 * ```
 */
typealias Skill = Supplier<String>
