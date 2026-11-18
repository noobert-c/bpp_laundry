package com.example.bpp_laundry

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bpp_laundry.auth.ForgotPasswordUiState
import com.example.bpp_laundry.auth.ForgotPasswordViewModel
import com.example.bpp_laundry.ui.components.AppBackButton
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppTextBlock
import com.example.bpp_laundry.ui.components.AppTextButtonRow
import com.example.bpp_laundry.ui.components.AppTextField
import com.example.bpp_laundry.ui.layout.AppFormLayout
import com.example.bpp_laundry.ui.layout.AppLayout

@Preview(
    name = "App Layout",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun ForgotPasswordscreen(
    onPrevious: () -> Unit = {},
    onKirimTautan: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var email by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ForgotPasswordUiState.OtpSent -> {
                val info = if (!state.otpCode.isNullOrBlank()) {
                    "${state.message}\n(Kode OTP: ${state.otpCode})"
                } else {
                    state.message
                }
                Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                onKirimTautan()
                viewModel.resetState()
            }
            is ForgotPasswordUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    AppLayout(
        topBar = {
            AppBackButton(onClick = onPrevious)
        },

        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AppTextBlock(
                    header = "Lupa kata sandi?",
                    body = "Masukkan email terdaftar Anda. Kami akan mengirimkan kode OTP untuk mengatur ulang kata sandi.",
                )

                Spacer(modifier = Modifier.height(4.dp))

                AppFormLayout {
                    AppTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "nama@email.com",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                        prefixIcon = Icons.Outlined.Email
                    )
                }

                if (uiState is ForgotPasswordUiState.Error) {
                    Text(
                        text = (uiState as ForgotPasswordUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val isLoading = uiState is ForgotPasswordUiState.Loading

                AppButton(
                    text = if (isLoading) "Memproses..." else "Kirim Kode OTP",
                    enabled = !isLoading,
                    onClick = {
                        viewModel.requestOtp(email)
                    }
                )
            }
        },

        bottomBar = {
            AppTextButtonRow(
                text = "Sudah punya akun?",
                buttonText = "Masuk",
                onClick = onBackToLogin,
                verticalAlignment = Alignment.Bottom
            )
        }
    )
}
