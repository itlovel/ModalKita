package com.example.modalkita.data.kyc

import com.example.modalkita.data.remote.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Kontrak repository KYC yang dipakai layer UI.
 */
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

/**
 * Model BARIS asli di tabel Supabase (TERENKRIPSI).
 * Semua field sensitif disimpan dalam bentuk cipher-text (String).
 */
@Serializable
private data class KycRow(
    @SerialName("id")
    val id: String, // sama dengan auth.users.id

    @SerialName("nik")
    val nikCipher: String,

    @SerialName("birth_date")
    val birthDateCipher: String,

    @SerialName("address")
    val addressCipher: String,

    @SerialName("ktp_photo_url")
    val ktpPhotoUrlCipher: String? = null,

    @SerialName("selfie_ktp_url")
    val selfieKtpUrlCipher: String? = null,

    @SerialName("is_verified")
    val isVerified: Boolean = false
)

private fun KycRow.toDomain(): KycProfile =
    KycProfile(
        id = id,
        nik = KycCrypto.decrypt(nikCipher),
        birthDate = KycCrypto.decrypt(birthDateCipher),
        address = KycCrypto.decrypt(addressCipher),
        ktpPhotoUrl = ktpPhotoUrlCipher?.let { KycCrypto.decrypt(it) },
        selfieKtpUrl = selfieKtpUrlCipher?.let { KycCrypto.decrypt(it) },
        isVerified = isVerified
    )

class SupabaseKycRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : KycRepository {

    /**
     * Ambil KYC milik user yang sedang login.
     * Mengandalkan RLS "hanya boleh akses baris milik sendiri".
     */
    override suspend fun getMyKyc(): KycProfile? {
        val user = client.auth.currentUserOrNull() ?: return null

        val rows = client.postgrest["kyc_profiles"]
            .select()          // RLS akan membatasi ke id = auth.uid()
            .decodeList<KycRow>()

        val row = rows.firstOrNull { it.id == user.id } ?: return null
        return row.toDomain()
    }

    /**
     * Insert atau update (upsert) data KYC milik user yang login.
     * Semua field disimpan dalam bentuk terenkripsi.
     */
    override suspend fun upsertMyKyc(
        nik: String,
        birthDate: String,
        address: String,
        ktpPhotoUrl: String?,
        selfieKtpUrl: String?
    ): KycProfile {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("Harus login terlebih dahulu")

        val encryptedRow = KycRow(
            id = user.id,
            nikCipher = KycCrypto.encrypt(nik),
            birthDateCipher = KycCrypto.encrypt(birthDate),
            addressCipher = KycCrypto.encrypt(address),
            ktpPhotoUrlCipher = ktpPhotoUrl?.let { KycCrypto.encrypt(it) },
            selfieKtpUrlCipher = selfieKtpUrl?.let { KycCrypto.encrypt(it) },
            isVerified = false      // default: belum diverifikasi admin
        )

        // upsert berdasarkan primary key `id`
        val savedRow = client.postgrest["kyc_profiles"]
            .upsert(encryptedRow) {   // tidak pakai onConflict agar aman di semua versi lib
                select()             // kembalikan row setelah upsert
            }
            .decodeSingle<KycRow>()

        return savedRow.toDomain()
    }
}
