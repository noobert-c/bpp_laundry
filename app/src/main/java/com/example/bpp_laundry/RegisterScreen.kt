package com.example.bpp_laundry.ui.preview

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
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
import com.example.bpp_laundry.auth.AuthViewModel
import com.example.bpp_laundry.auth.RegisterUiState
import com.example.bpp_laundry.ui.components.AppBackButton
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppPasswordField
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
fun RegisterScreen(
    onDaftar: () -> Unit = {},
    onMasuk: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsState()

    var nama by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var phone by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    // Tangani perubahan status registrasi (Sukses / Gagal)
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onDaftar()
                viewModel.resetRegisterState()
            }
            is RegisterUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    AppLayout(
        topBar = {
            AppBackButton(onClick = onMasuk)
        },

        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AppTextBlock(
                    header = "Buat akun baru",
                    body = "Daftarkan usaha laundry Anda dan mulai kelola pesanan hari ini.",
                )

                Spacer(modifier = Modifier.height(4.dp))

                AppFormLayout {
                    AppTextField(
                        label = "Nama Lengkap",
                        value = nama,
                        onValueChange = { nama = it },
                        placeholder = "Masukkan nama Lengkap",
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        prefixIcon = Icons.Outlined.PersonOutline
                    )

                    AppTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "nama@email.com",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        prefixIcon = Icons.Outlined.Email
                    )

                    AppTextField(
                        label = "Nomor Telepon (Opsional)",
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = "08123456789",
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next,
                        prefixIcon = Icons.Outlined.Phone
                    )

                    AppPasswordField(
                        value = password,
                        onValueChange = { password = it },
                    )
                }

                // Tampilkan pesan error di bawah input jika ada
                if (registerState is RegisterUiState.Error) {
                    Text(
                        text = (registerState as RegisterUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val isLoading = registerState is RegisterUiState.Loading

                AppButton(
                    text = if (isLoading) "Mendaftarkan..." else "Daftar",
                    enabled = !isLoading,
                    onClick = {
                        viewModel.register(nama, email, phone, password)
                    }
                )
            }
        },

        bottomBar = {
            AppTextButtonRow(
                text = "Sudah punya akun?",
                buttonText = "Masuk",
                onClick = onMasuk,
                verticalAlignment = Alignment.Bottom
            )
        }
    )
}
