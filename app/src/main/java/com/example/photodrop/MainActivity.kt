package com.example.photodrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.photodrop.ui.foto.FotoAufnahmeScreen
import com.example.photodrop.ui.theme.PhotoDropTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoDropTheme {
                FotoAufnahmeScreen()
            }
        }
    }
}
