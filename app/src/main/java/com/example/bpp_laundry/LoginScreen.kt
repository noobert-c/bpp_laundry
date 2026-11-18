package com.example.bpp_laundry.ui.preview

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bpp_laundry.R
import com.example.bpp_laundry.auth.AuthViewModel
import com.example.bpp_laundry.auth.LoginUiState
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppPasswordField
import com.example.bpp_laundry.ui.components.AppTextBlock
import com.example.bpp_laundry.ui.components.AppTextButton
import com.example.bpp_laundry.ui.components.AppTextButtonRow
import com.example.bpp_laundry.ui.components.AppTextField
import com.example.bpp_laundry.ui.layout.AppFormLayout
import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.bpp_laundry.ui.theme.AppText

@Preview(
    name = "App Layout",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun LoginScreen(
    onLanjutkan: () -> Unit = {},
    onDaftar: () -> Unit = {},
    onLupaSandi: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    // Tangani perubahan status login (Sukses / Gagal)
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onLanjutkan()
                viewModel.resetState()
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    AppLayout(
        topBar = {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "App Icon",
                modifier = Modifier.size(40.dp)
            )
        },

        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AppTextBlock(
                    header = "Catat pesanan dalam hitungan detik",
                    body = "Input pelanggan, jenis layanan, dan berat cucian dengan cepat. Kode pesanan dibuat otomatis.",
                )

                Spacer(modifier = Modifier.height(4.dp))

                AppFormLayout {
                    AppTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "nama@email.com",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        prefixIcon = Icons.Outlined.Email
                    )

                    AppPasswordField(
                        value = password,
                        onValueChange = { password = it },
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        AppTextButton(
                            "Lupa kata sandi?",
                            onClick = onLupaSandi
                        )
                    }
                }

                // Tampilkan pesan error di bawah input jika ada
                if (loginState is LoginUiState.Error) {
                    Text(
                        text = (loginState as LoginUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val isLoading = loginState is LoginUiState.Loading

                AppButton(
                    text = if (isLoading) "Memeriksa..." else "Lanjutkan",
                    enabled = !isLoading,
                    onClick = {
                        viewModel.login(email, password)
                    }
                )
            }
        },

        bottomBar = {
            AppTextButtonRow(
                text = "Belum punya akun?",
                buttonText = "Daftar",
                onClick = onDaftar,
                verticalAlignment = Alignment.Bottom
            )
        }
    )
}
