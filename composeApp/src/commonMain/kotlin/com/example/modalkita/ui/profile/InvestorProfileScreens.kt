package com.example.modalkita.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.data.auth.UserProfile
import com.example.modalkita.data.kyc.*
import com.example.modalkita.data.remote.SupabaseClientProvider
import com.example.modalkita.ui.components.ModalKitaChildTopBar
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
//import io.github.jan.supabase.postgrest.decodeList
import kotlinx.coroutines.launch

/* ============================================================
   ======================= PROFILE ROOT =======================
   ============================================================ */

private enum class ProfileSubScreen {
    MAIN,
    KYC,
    EDIT
}

@Composable
fun InvestorProfileRoot(
    modifier: Modifier = Modifier,
    kycRepository: KycRepository = SupabaseKycRepository(),
    onLoggedOut: () -> Unit        // <-- diperbaiki: harus () -> Unit
) {
    val scope = rememberCoroutineScope()

    var subScreen by remember { mutableStateOf(ProfileSubScreen.MAIN) }
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var kycProfile by remember { mutableStateOf<KycProfile?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Ambil profile + KYC dari Supabase sekali di awal
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val client = SupabaseClientProvider.client
            val user = client.auth.currentUserOrNull()

            if (user != null) {
                // Dengan RLS: hanya mengembalikan baris milik user yang login
                val rows = client.postgrest["user_profiles"]
                    .select()
                    .decodeList<UserProfile>()

                profile = rows.firstOrNull()
            }

            kycProfile = kycRepository.getMyKyc()
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat profil."
        } finally {
            isLoading = false
        }
    }

    when (subScreen) {
        ProfileSubScreen.MAIN -> InvestorProfileMainScreen(
            profile = profile,
            kycProfile = kycProfile,
            isLoading = isLoading,
            error = error,
            onOpenKyc = { subScreen = ProfileSubScreen.KYC },
            onOpenEditProfile = { subScreen = ProfileSubScreen.EDIT },
            onLogout = {
                scope.launch {
                    SupabaseClientProvider.client.auth.signOut()
                    onLoggedOut()
                }
            },
            modifier = modifier
        )

        ProfileSubScreen.KYC -> InvestorKycScreen(
            existing = kycProfile,
            kycRepository = kycRepository,
            onBack = { subScreen = ProfileSubScreen.MAIN },
            onSaved = { saved ->
                kycProfile = saved
                subScreen = ProfileSubScreen.MAIN
            },
            modifier = modifier
        )

        ProfileSubScreen.EDIT -> InvestorEditProfileScreen(
            existingProfile = profile,
            onBack = { subScreen = ProfileSubScreen.MAIN },
            onProfileUpdated = { updated ->
                profile = updated
                subScreen = ProfileSubScreen.MAIN
            },
            modifier = modifier
        )
    }
}

/* ============================================================
   ====================== PROFILE MAIN ========================
   ============================================================ */

@Composable
private fun InvestorProfileMainScreen(
    profile: UserProfile?,
    kycProfile: KycProfile?,
    isLoading: Boolean,
    error: String?,
    onOpenKyc: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    val displayName = profile?.fullName ?: "Investor ModalKita"
    val username = profile?.phone?.let { "@$it" } ?: "@investor"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                color = ModalKitaColors.White50,
                shadowElevation = 4.dp,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Profil",
                            style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = ModalKitaColors.Black500
                        )
                        Text(
                            text = "Edit atau lengkapi data Anda",
                            style = typography.bodySmall,
                            color = ModalKitaColors.Black300
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Card nama besar
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ModalKitaColors.Green600),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayName,
                        style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = ModalKitaColors.White50,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = username,
                        style = typography.bodySmall,
                        color = ModalKitaColors.White50
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ModalKitaColors.Green600)
                }
            }

            if (error != null) {
                Text(
                    text = error,
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Card "Lengkapi KYC"
            ProfileActionCard(
                title = "Lengkapi KYC",
                subtitle = when {
                    kycProfile?.isVerified == true ->
                        "KYC sudah terverifikasi"
                    kycProfile != null ->
                        "KYC sudah diisi, menunggu verifikasi"
                    else ->
                        "Lengkapi data KYC Anda"
                },
                onClick = onOpenKyc
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Card "Edit Profil"
            ProfileActionCard(
                title = "Edit Profil",
                subtitle = "Ubah nama atau nomor HP",
                onClick = onOpenEditProfile
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                label = "Log Out",
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val typography = modalKitaTypography()
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ModalKitaColors.Black500
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = typography.bodySmall,
                    color = ModalKitaColors.Black300
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = ModalKitaColors.Green600
            )
        }
    }
}

/* ============================================================
   ======================= KYC SCREEN =========================
   ============================================================ */

@Composable
private fun InvestorKycScreen(
    existing: KycProfile?,
    kycRepository: KycRepository,
    onBack: () -> Unit,
    onSaved: (KycProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    val scope = rememberCoroutineScope()

    var nik by remember { mutableStateOf(existing?.nik ?: "") }
    var birthDate by remember { mutableStateOf(existing?.birthDate ?: "") }
    var address by remember { mutableStateOf(existing?.address ?: "") }

    var hasKtpPhoto by remember { mutableStateOf(existing?.ktpPhotoUrl != null) }
    var hasSelfieKtp by remember { mutableStateOf(existing?.selfieKtpUrl != null) }
    var declarationChecked by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ModalKitaChildTopBar(
            title = "Lengkapi KYC",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nik,
                onValueChange = { nik = it.filter(Char::isDigit) },
                label = { Text("NIK (Nomor KTP)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ModalKitaColors.Green600,
                    unfocusedBorderColor = ModalKitaColors.Green600
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Tanggal Lahir (dd/mm/yyyy)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ModalKitaColors.Green600,
                    unfocusedBorderColor = ModalKitaColors.Green600
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat (Sesuai KTP)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ModalKitaColors.Green600,
                    unfocusedBorderColor = ModalKitaColors.Green600
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            UploadBox(
                title = "Upload Foto KTP",
                hint = "Upload Foto (Max. 5MB)",
                isUploaded = hasKtpPhoto,
                onClick = {
                    // TODO: Integrasi file picker + Supabase Storage
                    hasKtpPhoto = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            UploadBox(
                title = "Upload Selfie + KTP",
                hint = "Upload Foto (Max. 5MB)",
                isUploaded = hasSelfieKtp,
                onClick = {
                    hasSelfieKtp = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = declarationChecked,
                    onCheckedChange = { declarationChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saya menyatakan data dan dokumen yang saya unggah benar dan valid.",
                    style = typography.bodySmall,
                    color = ModalKitaColors.Black300
                )
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                label = if (isSaving) "Menyimpan..." else "Simpan",
                onClick = {
                    val input = KycInput(
                        nik = nik,
                        birthDate = birthDate,
                        address = address,
                        hasKtpPhoto = hasKtpPhoto,
                        hasSelfieKtp = hasSelfieKtp,
                        declarationChecked = declarationChecked
                    )
                    val result = KycContractRules.validate(input)
                    if (!result.isAllowed) {
                        error = result.reasonIfRejected
                        return@PrimaryButton
                    }

                    scope.launch {
                        try {
                            isSaving = true
                            error = null

                            val saved = kycRepository.upsertMyKyc(
                                nik = nik,
                                birthDate = birthDate,
                                address = address,
                                ktpPhotoUrl = existing?.ktpPhotoUrl
                                    ?: if (hasKtpPhoto) "dummy-ktp-url" else null,
                                selfieKtpUrl = existing?.selfieKtpUrl
                                    ?: if (hasSelfieKtp) "dummy-selfie-url" else null
                            )

                            onSaved(saved)
                        } catch (e: Exception) {
                            error = e.message ?: "Gagal menyimpan data KYC."
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UploadBox(
    title: String,
    hint: String,
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    val typography = modalKitaTypography()
    Column {
        Text(
            text = title,
            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = ModalKitaColors.Black500
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ModalKitaColors.LightGreen100)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isUploaded) "File sudah diunggah (dummy)" else hint,
                style = typography.bodySmall,
                color = ModalKitaColors.Green600,
                textAlign = TextAlign.Center
            )
        }
    }
}

/* ============================================================
   =================== EDIT PROFILE SIMPLE ====================
   ============================================================ */

@Composable
private fun InvestorEditProfileScreen(
    existingProfile: UserProfile?,
    onBack: () -> Unit,
    onProfileUpdated: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(existingProfile?.fullName ?: "") }
    var phone by remember { mutableStateOf(existingProfile?.phone ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ModalKitaChildTopBar(
            title = "Edit Profil",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ModalKitaColors.Green600,
                    unfocusedBorderColor = ModalKitaColors.Green600
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.filter { ch -> ch.isDigit() || ch == '+' } },
                label = { Text("No. HP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ModalKitaColors.Green600,
                    unfocusedBorderColor = ModalKitaColors.Green600
                )
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                label = if (isSaving) "Menyimpan..." else "Simpan Perubahan",
                onClick = {
                    if (fullName.isBlank()) {
                        error = "Nama tidak boleh kosong."
                        return@PrimaryButton
                    }

                    scope.launch {
                        try {
                            isSaving = true
                            error = null

                            val client = SupabaseClientProvider.client
                            val user = client.auth.currentUserOrNull()
                                ?: throw IllegalStateException("Belum login.")

                            val updatedRow = client.postgrest["user_profiles"]
                                .update(
                                    mapOf(
                                        "full_name" to fullName,
                                        "phone" to phone
                                    )
                                ) {
                                    // Tidak pakai eq(); RLS batasi otomatis ke user ini
                                    select()
                                }
                                .decodeList<UserProfile>()
                                .first()

                            onProfileUpdated(updatedRow)
                        } catch (e: Exception) {
                            error = e.message ?: "Gagal menyimpan profil."
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
