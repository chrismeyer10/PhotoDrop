package com.example.photodrop.ui.drive.zustand

import androidx.compose.runtime.Composable
import com.example.photodrop.ui.drive.api.DriveOrdner

// Waehlt den passenden Inhalt fuer den aktuellen Drive-Zustand aus.
@Composable
fun ZustandInhaltAuswaehlen(
    zustand: DriveZustand,
    navigationsStack: List<DriveOrdner> = emptyList(),
    aktiverOrdner: DriveOrdner? = null,
    onVerbinden: () -> Unit,
    onOrdnerAuswaehlen: (DriveOrdner) -> Unit,
    onNeuenOrdnerErstellen: () -> Unit,
    onOrdnerBestaetigen: (String) -> Unit,
    onOrdnerBenennenAbbrechen: () -> Unit = {},
    onZuruecksetzen: () -> Unit,
    onLadeAbbrechen: () -> Unit = {},
    onOrdnerWechseln: () -> Unit = {},
    onOrdnerOeffnen: (DriveOrdner) -> Unit = {},
    onOrdnerAlsZielSetzen: (DriveOrdner) -> Unit = {},
    onZurueckNavigieren: () -> Unit = {}
) {
    when (zustand) {
        is DriveZustand.NichtVerbunden -> NichtVerbundenInhalt(onVerbinden)
        is DriveZustand.Verbindet -> LadeInhalt(onAbbrechen = onLadeAbbrechen)
        is DriveZustand.OrdnerLaden -> LadeInhalt(onAbbrechen = onLadeAbbrechen)
        is DriveZustand.OrdnerAuswaehlen -> OrdnerAuswaehlenInhalt(
            zustand = zustand,
            onAuswaehlen = onOrdnerAuswaehlen,
            onNeuErstellen = onNeuenOrdnerErstellen
        )
        is DriveZustand.OrdnerBenennen -> OrdnerBenennenInhalt(
            kontoName = zustand.kontoName,
            onBestaetigen = onOrdnerBestaetigen,
            onAbbrechen = onOrdnerBenennenAbbrechen
        )
        is DriveZustand.Verbunden -> VerbundenAnimiertInhalt(zustand)
        is DriveZustand.InhaltGeladen -> OrdnerInhaltInhalt(
            zustand = zustand,
            navigationsStack = navigationsStack,
            aktiverOrdner = aktiverOrdner,
            onOrdnerOeffnen = onOrdnerOeffnen,
            onOrdnerAlsZielSetzen = onOrdnerAlsZielSetzen,
            onZurueckNavigieren = onZurueckNavigieren,
            onOrdnerWechseln = onOrdnerWechseln
        )
        is DriveZustand.Fehler -> FehlerInhalt(zustand.meldung, onZuruecksetzen)
    }
}
