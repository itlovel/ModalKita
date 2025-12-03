package com.example.modalkita.data.loan

import com.example.modalkita.data.remote.SupabaseClientProvider
import io.github.jan.supabase.storage.storage

interface LoanDocumentsRepository {
    suspend fun uploadSupportingDoc(
        borrowerId: String,
        fileName: String,
        bytes: ByteArray
    ): String
}

class SupabaseLoanDocumentsRepository : LoanDocumentsRepository {

    private val client get() = SupabaseClientProvider.client

    override suspend fun uploadSupportingDoc(
        borrowerId: String,
        fileName: String,
        bytes: ByteArray
    ): String {
        val bucket = client.storage.from("loan_docs")

        val safeFileName = fileName.ifBlank { "supporting_doc.pdf" }
        val path = "$borrowerId/$safeFileName"

        bucket.upload(
            path = path,
            data = bytes
        ) {
            upsert = true
        }

        return bucket.publicUrl(path)
    }
}
