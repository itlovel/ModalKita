@file:OptIn(ExperimentalEncodingApi::class)

package com.example.modalkita.data.kyc

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Util enkripsi sangat sederhana untuk demo eKYC.
 *
 * - Menggunakan XOR dengan key statis + Base64.
 * - TUJUAN: menunjukkan bahwa data NIK, alamat, dsb
 *   TIDAK disimpan dalam bentuk plaintext di database.
 *
 * Di laporan jelaskan dengan jujur bahwa:
 * - Key hardcoded dan skema XOR tidak aman untuk produksi.
 * - Untuk real system seharusnya pakai AES/GCM atau libs siap pakai
 *   dengan key management yang benar (KMS, dsb).
 */
object KycCrypto {

    // Kunci dummy (hardcoded) hanya untuk demo tugas.
    private const val KEY = "ModalKitaSuperSecretKey"

    private fun xorBytes(input: ByteArray): ByteArray {
        val keyBytes = KEY.encodeToByteArray()
        val out = ByteArray(input.size)
        for (i in input.indices) {
            val b = input[i].toInt()
            val k = keyBytes[i % keyBytes.size].toInt()
            out[i] = (b xor k).toByte()
        }
        return out
    }

    fun encrypt(plain: String): String {
        val bytes = plain.encodeToByteArray()
        val xored = xorBytes(bytes)
        return Base64.encode(xored)
    }

    fun decrypt(cipher: String): String {
        val decoded = Base64.decode(cipher)
        val xored = xorBytes(decoded)
        return xored.decodeToString()
    }
}