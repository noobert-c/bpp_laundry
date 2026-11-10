package com.example.bpp_laundry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.bpp_laundry.ui.components.AppBackButton
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppTextButton
import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.ui.theme.AppColors

private object OtpSuccessColors {
    val Primary = Color(0xFF0F7B6C)
    val OnPrimary = Color.White
    val SuccessTint = Color(0xFFD1F5EC)
}

@Composable
fun OtpSuccessScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
) {
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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                SuccessIndicator()

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Verifikasi berhasil",
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextMain,
                    )

                    Text(
                        text = "Kode OTP berhasil diverifikasi. Lanjutkan untuk mengatur ulang kata sandi Anda.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = AppColors.TextSecondary,
                    )
                }

                AppButton(
                    text = "Lanjutkan",
                    onClick = onContinueClick,
                )
            }
        },

        bottomBar = {
            AppTextButton(
                text = "Kembali ke halaman Masuk",
                onClick = onBackToLoginClick,
            )
        },
    )
}

@Composable
private fun SuccessIndicator() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(OtpSuccessColors.SuccessTint),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(OtpSuccessColors.Primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Berhasil",
                tint = OtpSuccessColors.OnPrimary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(
    name = "Phone 390x844",
    widthDp = 390,
    heightDp = 844,
    showBackground = true,
)
@Composable
private fun OtpSuccessPhonePreview() {
    OtpSuccessScreen()
}

@Preview(
    name = "Small phone 320x568",
    widthDp = 320,
    heightDp = 568,
    showBackground = true,
)
@Composable
private fun OtpSuccessSmallPreview() {
    OtpSuccessScreen()
}

@Preview(
    name = "Tablet 800x1280",
    widthDp = 800,
    heightDp = 1280,
    showBackground = true,
)
@Composable
private fun OtpSuccessTabletPreview() {
    OtpSuccessScreen()
}