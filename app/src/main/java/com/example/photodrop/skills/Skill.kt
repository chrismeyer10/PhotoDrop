package com.example.photodrop.skills

import java.util.function.Supplier

// Ein Skill ist ein Tool das Claude waehrend eines Agent-Laufs aufrufen kann.
// Implementiere Supplier<String> und registriere den Skill in SkillRegistry.
typealias Skill = Supplier<String>
