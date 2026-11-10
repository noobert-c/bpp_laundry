package com.example.bpp_laundry.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.R
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.OnBoardingIndicator
import com.example.bpp_laundry.ui.layout.OnBoardingLayout
import com.example.bpp_laundry.ui.theme.AppText

@Composable
fun OnBoarding_3(
    onSkip: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val primaryColor = Color(0xFF128577)
    val backgroundColor = Color(0xFFF5F7F8)

    OnBoardingLayout(
        backgroundColor = backgroundColor,

        // PADDING TOP BAR

        // TOP BAR
        topBar = {

        },

        // BOTTOM BUTTON
        bottomButton = {
            AppButton(
                text = "Mulai Sekarang",
                onClick = onNext
            )
        }
    ) {

        // CONTENT
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ILLUSTRATION
            Image(
                painter = painterResource(
                    id = R.drawable.ic_onboarding_3_illustration
                ),
                contentDescription = "Onboarding Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.9f),
                alignment = Alignment.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // TITLE
            Text(
                text = "Laporan usaha yang mudah dibaca",
                style = AppText.Header,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // DESCRIPTION
            Text(
                text = "Lihat pendapatan harian dan bulanan, pelanggan setia, serta performa laundry Anda kapan saja.",
                style = AppText.Body,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // PAGE INDICATOR
            OnBoardingIndicator(activeIndex = 2)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
