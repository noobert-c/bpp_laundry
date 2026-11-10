package com.example.bpp_laundry

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.theme.AppText

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        // ===== BACKGROUND CIRCLES =====

        // Besar - kanan atas (300dp), sebagian keluar layar di kanan & atas
        Image(
            painter = painterResource(id = R.drawable.ic_bg_circle),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 85.dp, y = (-140).dp)
                .size(300.dp)
        )

        // Kecil - kiri atas (70dp)
        Image(
            painter = painterResource(id = R.drawable.ic_bg_circle),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 38.dp, y = 141.dp)
                .size(70.dp)
        )

        // Besar - kiri bawah (240dp), sebagian keluar layar di kiri & bawah
        Image(
            painter = painterResource(id = R.drawable.ic_bg_circle),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-105).dp, y = 93.dp)
                .size(240.dp)
        )

        // ===== KONTEN TENGAH =====
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x=0.dp,y=-30.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: Logo app
            Box {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TODO: Teks "BPP Laundry"
            Box {
                Text(
                    text = "BPP Laundry",
                    style = AppText.Header
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TODO: Teks tagline
            Box {
                Text(
                    text = "Kelola laundry, lebih mudah.",
                    style = AppText.Body
                )
            }
        }

        // ===== FOOTER =====
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            // TODO: Teks "Versi 1.0.0"
            Text(
                text = "Versi 1.0.0",
                style = AppText.Footer
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 792)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}