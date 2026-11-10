package com.example.bpp_laundry.ui.preview


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.R
import com.example.bpp_laundry.ui.components.AppBackButton
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppPasswordField
import com.example.bpp_laundry.ui.components.AppTextBlock
import com.example.bpp_laundry.ui.components.AppTextButton
import com.example.bpp_laundry.ui.components.AppTextButtonRow
import com.example.bpp_laundry.ui.components.AppTextField
import com.example.bpp_laundry.ui.layout.AppFormLayout
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.bpp_laundry.ui.theme.AppText

@Preview(
    name = "App Layout",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun RegisterScreen(
    onDaftar: ()-> Unit = {},
    onMasuk: ()-> Unit = {}

) {
    AppLayout(
        topBar = {
            AppBackButton(onClick = {})
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

                var nama by rememberSaveable {
                    mutableStateOf("")
                }

                var email by rememberSaveable {
                    mutableStateOf("")
                }

                var password by rememberSaveable {
                    mutableStateOf("")
                }

                AppFormLayout {
                    AppTextField(
                        label = "Nama Lengkap",
                        value = nama,
                        onValueChange = { nama = it },
                        placeholder = "Masukkan nama Lengkap",
                        keyboardType = KeyboardType.Email,
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
                            onClick = {}
                        )
                    }
                }



                AppButton("Daftar", onClick = onDaftar)



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
