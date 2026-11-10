package com.example.bpp_laundry.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.R
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppTextButton
import com.example.bpp_laundry.ui.components.OnBoardingIndicator
import com.example.bpp_laundry.ui.layout.OnBoardingLayout
import com.example.bpp_laundry.ui.theme.AppText

@Composable
fun OnBoarding_2(
    onSkip: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val primaryColor = Color(0xFF128577)
    val backgroundColor = Color(0xFFF5F7F8)

    OnBoardingLayout(
        backgroundColor = backgroundColor,

        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                AppTextButton(text= "Lewati",onClick= onSkip)
            }
        },

        bottomButton = {
            AppButton(
                text = "Lanjut",
                onClick = onNext
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_onboarding_2_illustration),
                contentDescription = "Onboarding Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.9f),
                alignment = Alignment.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Pantau status setiap cucian",
                style = AppText.Header,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Dari diterima, dicuci, disetrika, hingga siap diambil — semua terlihat jelas dalam satu layar.",
                style = AppText.Body,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            OnBoardingIndicator(activeIndex = 1)

            Spacer(modifier = Modifier.height(20.dp))   // ✅ sekarang di dalam Column
        }
    }
}
