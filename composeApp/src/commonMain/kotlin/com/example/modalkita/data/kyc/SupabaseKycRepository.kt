package com.example.modalkita.data.kyc

import com.example.modalkita.data.remote.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================
//  Interface Repository yang dipakai di UI (InvestorProfileScreen)
// ============================================================

interface KycRepository {
    suspend fun getMyKyc(): KycProfile?

    suspend fun upsertMyKyc(
        nik: String,
        birthDate: String,
        address: String,
        ktpPhotoUrl: String?,
        selfieKtpUrl: String?
    ): KycProfile
}

// DTO untuk upsert ke Supabase, mengikuti struktur tabel kyc_profiles
@Serializable
private data class KycUpsertDto(
    @SerialName("id")
    val id: String,   // sama dengan auth.users.id & PK di kyc_profiles

    @SerialName("nik")
    val nik: String,

    @SerialName("birth_date")
    val birthDate: String,

    @SerialName("address")
    val address: String,

    @SerialName("ktp_photo_url")
    val ktpPhotoUrl: String? = null,

    @SerialName("selfie_ktp_url")
    val selfieKtpUrl: String? = null
)

class SupabaseKycRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : KycRepository {

    // --------------------------------------------------------
    // Ambil KYC milik user yang sedang login
    // RLS di Supabase sebaiknya:
    //   SELECT:  id = auth.uid()
    // --------------------------------------------------------
    override suspend fun getMyKyc(): KycProfile? {
        val rows = client.postgrest["kyc_profiles"]
            .select()
            .decodeList<KycProfile>()   // KycProfile dari KycModel.kt

        return rows.firstOrNull()
    }

    // --------------------------------------------------------
    // Insert / update (upsert) data KYC user saat ini
    // --------------------------------------------------------
    override suspend fun upsertMyKyc(
        nik: String,
        birthDate: String,
        address: String,
        ktpPhotoUrl: String?,
        selfieKtpUrl: String?
    ): KycProfile {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("Harus login terlebih dahulu.")

        val dto = KycUpsertDto(
            id = user.id,
            nik = nik,
            birthDate = birthDate,
            address = address,
            ktpPhotoUrl = ktpPhotoUrl,
            selfieKtpUrl = selfieKtpUrl
        )

        // Upsert berdasarkan primary key "id".
        // Tidak perlu pakai onConflict, biarkan pakai PK default.
        return client.postgrest["kyc_profiles"]
            .upsert(dto) {
                select()   // minta row hasil upsert dikembalikan
            }
            .decodeSingle<KycProfile>()
    }
}
