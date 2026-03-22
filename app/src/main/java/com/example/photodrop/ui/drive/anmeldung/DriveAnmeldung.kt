package com.example.photodrop.ui.drive.anmeldung

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Kapselt die Google Sign-In Logik fuer die Drive-Verbindung.
object DriveAnmeldung {

    private const val DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file"

    // Erstellt den Google Sign-In Intent mit Drive-Berechtigung.
    fun intentErstellen(context: Context): Intent {
        val optionen = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_SCOPE))
            .build()
        return GoogleSignIn.getClient(context, optionen).signInIntent
    }

    // Holt das zuletzt angemeldete Konto oder null.
    fun letztesKontoHolen(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    // Holt den OAuth2-Token fuer das gegebene Konto.
    suspend fun tokenHolen(context: Context, konto: GoogleSignInAccount): String =
        withContext(Dispatchers.IO) {
            GoogleAuthUtil.getToken(
                context, konto.account!!,
                "oauth2:$DRIVE_SCOPE"
            )
        }

    // Meldet den Nutzer ab und ruft den Callback auf wenn fertig.
    fun abmelden(context: Context, onFertig: () -> Unit) {
        val optionen = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_SCOPE))
            .build()
        GoogleSignIn.getClient(context, optionen)
            .signOut().addOnCompleteListener { onFertig() }
    }
}
