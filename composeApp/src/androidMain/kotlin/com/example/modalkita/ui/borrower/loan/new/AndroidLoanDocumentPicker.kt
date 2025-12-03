package com.example.modalkita.ui.borrower.loan.new

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.modalkita.data.loan.LoanDocumentsRepository
import com.example.modalkita.data.loan.SupabaseLoanDocumentsRepository
import com.example.modalkita.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth

@Composable
fun AndroidLoanDocumentPicker(
    viewModel: NewLoanViewModel,
    content: @Composable (
        onPickClick: () -> Unit,
        selectedFileName: String?,
        isUploading: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedName by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val docsRepository: LoanDocumentsRepository = remember {
        SupabaseLoanDocumentsRepository()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                scope.launch {
                    isUploading = true
                    try {
                        val fileName = queryFileName(context, uri) ?: "supporting_doc.pdf"
                        selectedName = fileName

                        val bytes = readBytes(context, uri)

                        val client = SupabaseClientProvider.client
                        val user = client.auth.currentUserOrNull()
                            ?: error("User not logged in")

                        val url = docsRepository.uploadSupportingDoc(
                            borrowerId = user.id,
                            fileName = fileName,
                            bytes = bytes
                        )

                        viewModel.setSupportingDocUrl(url)
                    } finally {
                        isUploading = false
                    }
                }
            }
        }
    )

    content(
        { launcher.launch(arrayOf("application/pdf")) }, // onPickClick
        selectedName,                                   // selectedFileName
        isUploading                                     // isUploading
    )
}

private fun queryFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) {
                name = it.getString(index)
            }
        }
    }
    return name
}

private fun readBytes(context: Context, uri: Uri): ByteArray {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        input.readBytes()
    } ?: ByteArray(0)
}
