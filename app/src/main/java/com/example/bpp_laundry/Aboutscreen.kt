package com.example.bpp_laundry



import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* -------------------------------------------------------------------------- */
/*  Design tokens (dari metadata Figma node 1:955)                            */
/* -------------------------------------------------------------------------- */

private object AboutColors {
    val Primary = Color(0xFF0F7B6C)
    val OnPrimary = Color.White
    val PrimaryTint = Color(0xFFD1F5EC)
    val Background = Color(0xFFF6F8FA)
    val Surface = Color.White
    val BorderSoft = Color(0xFFE2E8F0)   // tombol kembali
    val Border = Color(0xFFDDE7E6)       // kartu fitur
    val Muted = Color(0xFF6B7280)
    val Text = Color(0xFF111827)
}

/**
 * Font Inter. Tambahkan file font ke res/font lalu ganti, contoh:
 * FontFamily(Font(R.font.inter_regular), Font(R.font.inter_medium, FontWeight.Medium), ...)
 */
private val Inter: FontFamily = FontFamily.Default

private val CardShape = RoundedCornerShape(16.dp)
private val ContentMaxWidth = 560.dp // batas lebar konten di tablet

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

private data class Feature(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

private val features = listOf(
    Feature(
        Icons.AutoMirrored.Outlined.Assignment, // clipboard-list
        "Pesanan lebih teratur",
        "Catat pesanan dan pantau progres cucian.",
    ),
    Feature(
        Icons.Outlined.People, // users
        "Pelanggan dalam satu tempat",
        "Simpan kontak dan riwayat transaksi.",
    ),
    Feature(
        Icons.Outlined.BarChart, // chart-no-axes-combined
        "Pembayaran & laporan",
        "Kelola tagihan dan ringkasan pendapatan.",
    ),
    Feature(
        Icons.Outlined.ChatBubbleOutline, // message-circle
        "Notifikasi pelanggan",
        "Siapkan pesan pembaruan via WhatsApp.",
    ),
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AboutScreen(
    versionName: String = "1.0.0", // isi dari BuildConfig.VERSION_NAME
    onBackClick: () -> Unit = {},  // -> Pengaturan (1:4193, asumsi)
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AboutColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        val minHeight = maxHeight

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = ContentMaxWidth)
                    .fillMaxWidth()
                    .heightIn(min = minHeight),
            ) {
                // ----- Judul halaman (tinggi 56) -----
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BackButton(onClick = onBackClick)
                    Text(
                        text = "Tentang Aplikasi",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            color = AboutColors.Text,
                        ),
                    )
                }

                // ----- Konten -----
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp), // content_gap
                ) {
                    AppIdentity(versionName = versionName)

                    Text(
                        text = "Asisten usaha laundry Anda. Kelola pesanan, pelanggan, dan pembayaran dengan lebih mudah dalam satu aplikasi.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 20.sp, // 1,4 x 14sp
                            color = AboutColors.Muted,
                        ),
                    )

                    FeatureCard()
                }

                // Mendorong footer ke bawah pada layar tinggi.
                Spacer(Modifier.weight(1f))

                // ----- Pengembang dan hak cipta -----
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FooterText("Dikembangkan oleh Tim BPP Laundry")
                    FooterText("© 2026 BPP Laundry. Hak cipta dilindungi.")
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Bagian-bagian layar                                                       */
/* -------------------------------------------------------------------------- */

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(AboutColors.Surface)
            .border(1.dp, AboutColors.BorderSoft, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Kembali",
            tint = AboutColors.Text,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** Logo 72dp (radius 20, ikon 40dp), nama aplikasi, dan badge versi (pil). */
@Composable
private fun AppIdentity(versionName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AboutColors.Primary),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "Logo BPP Laundry",
                modifier = Modifier.size(40.dp),
            )
        }

        Text(
            text = "BPP Laundry",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = AboutColors.Text,
            ),
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(AboutColors.PrimaryTint)
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(
                text = "Versi $versionName",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = AboutColors.Primary,
                ),
            )
        }
    }
}

/** Kartu fitur: putih, border 1dp #DDE7E6, radius 16, padding 16, gap 16. */
@Composable
private fun FeatureCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(AboutColors.Surface)
            .border(1.dp, AboutColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Fitur utama",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = AboutColors.Text,
            ),
        )

        features.forEach { FeatureRow(it) }
    }
}

/** Item fitur: gap 12, ikon dalam kotak 36dp (radius 12) / 20dp. */
@Composable
private fun FeatureRow(feature: Feature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AboutColors.PrimaryTint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = AboutColors.Primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = feature.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = AboutColors.Text,
                ),
            )
            Text(
                text = feature.description,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = AboutColors.Muted,
                ),
            )
        }
    }
}

@Composable
private fun FooterText(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = AboutColors.Muted,
        ),
    )
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 390x844", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun AboutPhonePreview() = AboutScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun AboutSmallPreview() = AboutScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun AboutTabletPreview() = AboutScreen()