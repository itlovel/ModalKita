package com.example.modalkita

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                openMidtransPayment = { url ->
                    // DEBUG: cek apakah URL kosong / null
                    println("Open Midtrans Payment: $url")

                    if (url.isNullOrBlank()) {
                        // sementara: jangan apa-apa, atau bisa tampilkan snackbar di layer Compose
                        return@App
                    }

                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    // kalau mau paksa ke browser:
                    // intent.addCategory(Intent.CATEGORY_BROWSABLE)
                    startActivity(intent)
                }
            )
        }
    }
}
