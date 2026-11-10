
package com.example.bpp_laundry.ui.preview
import com.example.bpp_laundry.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppChip
import com.example.bpp_laundry.ui.components.*

import com.example.bpp_laundry.ui.components.AppTopSpacer
import com.example.bpp_laundry.ui.components.AppTopSpacer
import com.example.bpp_laundry.ui.theme.AppText


@Composable
fun TypographyPreviewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {


        AppTopSpacer()
        Text(
            text = "Typography Preview",
            style = AppText.Header
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Preview komponen teks aplikasi BPP Laundry.",
            style = AppText.Body
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Header 24 Bold",
                    style = AppText.Header
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Body 14 Regular",
                    style = AppText.Body
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "BPP Laundry menyediakan layanan " +
                            "laundry yang praktis dan terpercaya.",
                    style = AppText.Body
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Contoh penggunaan",
            style = AppText.Header
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Selamat datang!",
            style = AppText.Header
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Kelola pesanan laundry Anda dengan mudah.",
            style = AppText.Body
        )
        AppChip(
            text = "Pesanan Baru",
            iconRes = R.drawable.ic_clock
        )
        Spacer(modifier = Modifier.height(6.dp))

        AppButton(
            text = "Lanjutkan",
            onClick = {
                // Aksi ketika tombol ditekan
            }
        )
        Spacer(modifier = Modifier.height(6.dp))

        AppButton(
            text = "Lanjutkan",
            onClick = {
                // Aksi tombol
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }


        )

        var email by rememberSaveable { mutableStateOf("") }

        AppTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "Masukkan email",
            prefixIcon = Icons.Default.Email
        )

    }
}
