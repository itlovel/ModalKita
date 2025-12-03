package com.example.modalkita.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    // GANTI dengan URL & anon key project-mu
    private const val SUPABASE_URL = "https://mhbhaesdeqnzpzccqmle.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1oYmhhZXNkZXFuenB6Y2NxbWxlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ2NzA0ODYsImV4cCI6MjA4MDI0NjQ4Nn0.YreqqnHBI0LJ6eNVfwW_6WYJXQLHvs0vx8Gr08lW9qI"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
