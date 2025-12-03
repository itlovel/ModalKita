package com.example.modalkita.data.kyc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Baris di tabel kyc_profiles Supabase
@Serializable
data class KycProfile(
    @SerialName("id")
    val id: String,            // sama dengan auth.users.id

    @SerialName("nik")
    val nik: String,

    @SerialName("birth_date")
    val birthDate: String,     // simpan sebagai string "dd/MM/yyyy" atau "yyyy-MM-dd"

    @SerialName("address")
    val address: String,

    @SerialName("ktp_photo_url")
    val ktpPhotoUrl: String? = null,

    @SerialName("selfie_ktp_url")
    val selfieKtpUrl: String? = null,

    @SerialName("is_verified")
    val isVerified: Boolean = false
)

// Input dari form KYC
data class KycInput(
    val nik: String,
    val birthDate: String,
    val address: String,
    val hasKtpPhoto: Boolean,
    val hasSelfieKtp: Boolean,
    val declarationChecked: Boolean
)

/**
 * "Smart contract" rules sederhana untuk KYC.
 * Di presentasi: ini adalah aturan otomatis untuk menerima / menolak KYC.
 */
object KycContractRules {

    data class Result(
        val isAllowed: Boolean,
        val reasonIfRejected: String? = null
    )

    fun validate(input: KycInput): Result {
        // NIK wajib 16 digit angka
        if (input.nik.length != 16 || !input.nik.all { it.isDigit() }) {
            return Result(false, "NIK harus terdiri dari 16 digit angka.")
        }

        if (input.birthDate.isBlank()) {
            return Result(false, "Tanggal lahir tidak boleh kosong.")
        }

        if (input.address.length < 10) {
            return Result(false, "Alamat harus diisi dengan jelas (minimal 10 karakter).")
        }

        if (!input.hasKtpPhoto) {
            return Result(false, "Foto KTP wajib diunggah.")
        }

        if (!input.hasSelfieKtp) {
            return Result(false, "Foto selfie + KTP wajib diunggah.")
        }

        if (!input.declarationChecked) {
            return Result(false, "Anda harus menyatakan bahwa data dan dokumen valid.")
        }

        return Result(true, null)
    }
}
