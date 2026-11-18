package com.example.bpp_laundry

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bpp_laundry.auth.ForgotPasswordUiState
import com.example.bpp_laundry.auth.ForgotPasswordViewModel
import com.example.bpp_laundry.ui.components.AppBackButton
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppPasswordField
import com.example.bpp_laundry.ui.components.AppTextButton
import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.ui.layout.AppFormLayout
import com.example.bpp_laundry.ui.theme.AppColors

private const val MIN_PASSWORD_LENGTH = 8

private fun isPasswordValid(password: String): Boolean =
    password.length >= MIN_PASSWORD_LENGTH &&
            password.any { it.isLetter() } &&
            password.any { it.isDigit() }

@Composable
fun NewPasswordScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (newPassword: String) -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val passwordValid = isPasswordValid(password)
    val mismatch = confirm.isNotEmpty() && confirm != password
    val canSave = passwordValid && confirm == password

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ForgotPasswordUiState.PasswordResetSuccess -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onSaveClick(password)
                viewModel.resetState()
            }
            is ForgotPasswordUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    AppLayout(
        horizontalPadding = 16.dp,
        verticalPadding = 8.dp,
        contentGap = 24.dp,

        topBar = {
            AppBackButton(onClick = onBackClick)
        },

        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // Header
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Buat kata sandi baru",
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                    )

                    Text(
                        text = "Buat kata sandi baru untuk menjaga keamanan akun Anda.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = AppColors.TextSecondary,
                    )
                }

                // Form
                AppFormLayout {
                    AppPasswordField(
                        value = password,
                        onValueChange = { password = it },
                    )

                    AppPasswordField(
                        value = confirm,
                        onValueChange = { confirm = it },
                    )

                    // Pesan validasi
                    if (password.isNotEmpty() && !passwordValid) {
                        Text(
                            text = "Minimal 8 karakter, kombinasi huruf dan angka.",
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary,
                        )
                    }

                    if (mismatch) {
                        Text(
                            text = "Kata sandi tidak cocok.",
                            fontSize = 12.sp,
                            color = AppColors.ErrorInput,
                        )
                    }

                    if (uiState is ForgotPasswordUiState.Error) {
                        Text(
                            text = (uiState as ForgotPasswordUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                val isLoading = uiState is ForgotPasswordUiState.Loading

                // Tombol simpan
                AppButton(
                    text = if (isLoading) "Menyimpan..." else "Simpan kata sandi baru",
                    enabled = canSave && !isLoading,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.resetPassword(password)
                    },
                )
            }
        },

        bottomBar = {
            AppTextButton(
                text = "Kembali ke halaman Masuk",
                onClick = onBackToLoginClick,
            )
        }
    )
}
