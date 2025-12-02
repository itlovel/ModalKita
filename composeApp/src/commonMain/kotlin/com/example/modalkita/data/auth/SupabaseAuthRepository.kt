package com.example.modalkita.data.auth

import com.example.modalkita.data.remote.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

class SupabaseAuthRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : AuthRepository {

    // ================== REGISTER BORROWER ==================
    override suspend fun signUpBorrower(data: BorrowerRegisterData): Result<Unit> =
        runCatching {
            // 1. Buat akun auth (email + password)
            client.auth.signUpWith(Email) {
                this.email = data.email
                this.password = data.password
            }

            // 2. Ambil user id dari session (boleh null kalau tabel punya DEFAULT auth.uid())
            val userId = client.auth.currentUserOrNull()?.id

            // 3. Susun row profile
            val profile = UserProfile(
                id = userId,                 // biarkan null jika kolom id default auth.uid()
                fullName = data.fullName,
                phone = data.phone,
                role = "BORROWER",
                businessName = data.businessName,
                city = data.city,
                sector = data.sector,
                businessDuration = data.businessDuration
            )

            // 4. Insert ke tabel user_profiles
            client.postgrest["user_profiles"].insert(profile)
        }

    // ================== REGISTER INVESTOR ==================
    override suspend fun signUpInvestor(data: InvestorRegisterData): Result<Unit> =
        runCatching {
            client.auth.signUpWith(Email) {
                this.email = data.email
                this.password = data.password
            }

            val userId = client.auth.currentUserOrNull()?.id

            val profile = UserProfile(
                id = userId,
                fullName = data.fullName,
                phone = data.phone,
                role = "INVESTOR"
                // field usaha boleh null untuk investor
            )

            client.postgrest["user_profiles"].insert(profile)
        }

    // ================== LOGIN (BORROWER & INVESTOR SAMA) ==================
    override suspend fun login(email: String, password: String): Result<Unit> =
        runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
}
