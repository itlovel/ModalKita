package com.example.modalkita.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.cio.CIO

object SupabaseClientHolder {

    // GANTI ini dengan URL & anon key project Supabase kamu
    private const val SUPABASE_URL = "https://XXXXXXXXXXXX.supabase.co"
    private const val SUPABASE_ANON_KEY = "YOUR_PUBLIC_ANON_KEY"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)

        // Untuk sekarang cukup CIO, fokus ke Android dulu
        httpEngine = CIO.create()
    }
}
