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
fun LoginScreen(
    onLanjutkan: () -> Unit = {},
    onDaftar: () -> Unit = {},
    onLupaSandi: () -> Unit = {},
) {
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

                var email by rememberSaveable {
                    mutableStateOf("")
                }

                var password by rememberSaveable {
                    mutableStateOf("")
                }

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



                AppButton("Lanjutkan", onClick = {})
                AppButton(
                    text = "Lanjut Dengan Google",
                    onClick = onLanjutkan,
                    backgroundColor = AppColors.Surface,
                    contentColor = AppColors.PrimaryApp,
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = AppColors.PrimaryApp
                    ),
                    leadingContent = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_icon),
                            contentDescription = "Google",
                            modifier = Modifier.size(20.dp)
                        )
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
