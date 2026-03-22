package com.example.photodrop.ui.drive.zustand

import androidx.compose.runtime.Composable
import com.example.photodrop.ui.drive.api.DriveOrdner

// Waehlt den passenden Inhalt fuer den aktuellen Drive-Zustand aus.
@Composable
fun ZustandInhaltAuswaehlen(
    zustand: DriveZustand,
    onVerbinden: () -> Unit,
    onOrdnerAuswaehlen: (DriveOrdner) -> Unit,
    onNeuenOrdnerErstellen: () -> Unit,
    onOrdnerBestaetigen: (String) -> Unit,
    onZuruecksetzen: () -> Unit
) {
    when (zustand) {
        is DriveZustand.NichtVerbunden -> NichtVerbundenInhalt(onVerbinden)
        is DriveZustand.Verbindet -> LadeInhalt()
        is DriveZustand.OrdnerLaden -> LadeInhalt()
        is DriveZustand.OrdnerAuswaehlen -> OrdnerAuswaehlenInhalt(
            zustand = zustand,
            onAuswaehlen = onOrdnerAuswaehlen,
            onNeuErstellen = onNeuenOrdnerErstellen
        )
        is DriveZustand.OrdnerBenennen -> OrdnerBenennenInhalt(
            kontoName = zustand.kontoName,
            onBestaetigen = onOrdnerBestaetigen
        )
        is DriveZustand.Verbunden -> VerbundenAnimiertInhalt(zustand)
        is DriveZustand.InhaltGeladen -> OrdnerInhaltInhalt(zustand)
        is DriveZustand.Fehler -> FehlerInhalt(zustand.meldung, onZuruecksetzen)
    }
}
