package com.example.bpp_laundry


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

/* -------------------------------------------------------------------------- */
/*  CATATAN PENTING                                                           */
/*  Metadata Figma halaman ini PARSIAL (nominal, nilai paket, catatan         */
/*  simulasi, warna, tipografi bernilai null). Nilai tersebut di sini adalah  */
/*  ASUMSI yang mengikuti palet & gaya halaman Dashboard / Daftar Pesanan.    */
/*  Teks yang diasumsikan ditandai komentar "ASUMSI".                         */
/*                                                                            */
/*  PENTING: QR di halaman ini HANYA SIMULASI (sesuai desain). Pola yang      */
/*  digambar bukan kode QR sungguhan dan tidak bisa dipindai. Untuk produksi, */
/*  ganti dengan QR dari penyedia pembayaran (mis. QRIS dinamis).             */
/* -------------------------------------------------------------------------- */

private object PaymentColors {
    val Primary = Color(0xFF0F766E)
    val OnPrimary = Color.White
    val PrimaryTint = Color(0xFFCCFBF1)
    val Background = Color(0xFFF8FAFC)   // ASUMSI: background frame null
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
    val NoticeBackground = Color(0xFFFEF3C7) // ASUMSI: gaya catatan simulasi
    val NoticeContent = Color(0xFFB45309)
    val QrDark = Color(0xFF0F172A)
}

/**
 * Font Inter. Tambahkan file font ke res/font lalu ganti, contoh:
 * FontFamily(Font(R.font.inter_regular), Font(R.font.inter_medium, FontWeight.Medium), ...)
 */
private val Inter: FontFamily = FontFamily.Default

private val CardShape = RoundedCornerShape(16.dp)
private val ButtonShape = RoundedCornerShape(12.dp)
private val ContentMaxWidth = 480.dp   // batas lebar di tablet
private val QrFrameMaxSize = 290.dp    // ukuran bingkai QR di desain
private const val QrInnerFraction = 148f / 290f // QR 148dp di dalam bingkai 290dp

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

data class PaymentQrUiState(
    val amountLabel: String = "Total Pembayaran", // ASUMSI: label null
    val amount: String = "Rp 32.000",             // ASUMSI: nominal null (sama dengan pesanan LF-0250)
    val packageType: String = "Cuci + Setrika",   // ASUMSI: nilai null
    val weight: String = "4,0 kg",                // ASUMSI: nilai null
    /** Bibit pola QR simulasi; pakai ID pesanan agar polanya stabil. */
    val qrSeed: String = "LF-0250",
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@Composable
fun PaymentQrScreen(
    state: PaymentQrUiState = PaymentQrUiState(),
    onBackClick: () -> Unit = {},             // -> kembali ke Tambah Pesanan (asumsi)
    onPaymentConfirmed: () -> Unit = {},      // -> Transaksi berhasil (1:1001, asumsi)
    confirmButtonLabel: String = "Simulasikan Pembayaran Berhasil", // ASUMSI: lihat catatan di bawah
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaymentColors.Background)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxSize(),
        ) {
            // ----- Header (tinggi 56) -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ASUMSI: tombol kembali tidak disebut di metadata (hanya "Judul halaman").
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PaymentColors.Surface)
                        .border(1.dp, PaymentColors.Border, CircleShape)
                        .clickable(role = Role.Button, onClick = onBackClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Kembali",
                        tint = PaymentColors.Text,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = "Pembayaran QR", // ASUMSI: judul null; memakai nama frame
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = PaymentColors.Text,
                    ),
                )
            }

            // ----- Konten (scroll) -----
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
            ) {
                PaymentCard(state)
            }

            // ----- Tombol konfirmasi (simulasi) -----
            // ASUMSI: tidak ada tombol di metadata, tetapi navigasi ke "Transaksi berhasil"
            // perlu pemicu. Di produksi, navigasi dipicu oleh status pembayaran dari server
            // (mis. webhook/polling), lalu tombol ini dihapus.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(ButtonShape)
                        .background(PaymentColors.Primary)
                        .clickable(role = Role.Button, onClick = onPaymentConfirmed),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = confirmButtonLabel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = PaymentColors.OnPrimary,
                        ),
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Kartu pembayaran (350 x 612 di desain): nominal, bingkai QR, catatan      */
/*  simulasi, dan informasi laundry.                                          */
/* -------------------------------------------------------------------------- */

@Composable
private fun PaymentCard(state: PaymentQrUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(PaymentColors.Surface)
            .border(1.dp, PaymentColors.Border, CardShape)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Nominal pembayaran
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = state.amountLabel,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = PaymentColors.Muted,
                ),
            )
            Text(
                text = state.amount,
                maxLines = 1,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = PaymentColors.Primary,
                ),
            )
        }

        QrFrame(seed = state.qrSeed)

        // Catatan simulasi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PaymentColors.NoticeBackground)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = PaymentColors.NoticeContent,
                modifier = Modifier.size(18.dp),
            )
            // ASUMSI: teks catatan null di metadata.
            Text(
                text = "QR ini hanya simulasi. Tidak ada pembayaran sungguhan yang diproses.",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = PaymentColors.NoticeContent,
                ),
            )
        }

        // Informasi laundry
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PaymentColors.Border),
            )
            InfoRow(label = "Tipe paket", value = state.packageType)
            InfoRow(label = "Berat pakaian", value = state.weight)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = PaymentColors.Muted,
            ),
        )
        Text(
            text = value,
            textAlign = TextAlign.End,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = PaymentColors.Text,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Bingkai QR: persegi hingga 290dp, berisi QR simulasi ~51% (148dp)         */
/* -------------------------------------------------------------------------- */

@Composable
private fun QrFrame(seed: String) {
    Box(
        modifier = Modifier
            .widthIn(max = QrFrameMaxSize)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CardShape)
            .background(PaymentColors.Surface)
            .border(1.dp, PaymentColors.Border, CardShape),
        contentAlignment = Alignment.Center,
    ) {
        SimulatedQr(
            seed = seed,
            modifier = Modifier
                .fillMaxWidth(QrInnerFraction)
                .aspectRatio(1f),
        )
    }
}

/**
 * Pola mirip QR (21 x 21 modul) dengan tiga penanda sudut. Hanya ilustrasi:
 * isi modul diacak dari [seed] dan TIDAK mengandung data pembayaran.
 */
@Composable
private fun SimulatedQr(seed: String, modifier: Modifier = Modifier) {
    val modules = 21
    val grid = remember(seed) {
        val random = Random(seed.hashCode())
        Array(modules) { BooleanArray(modules) { random.nextBoolean() } }
    }

    Canvas(
        modifier = modifier.semantics { contentDescription = "Kode QR simulasi" },
    ) {
        val cell = size.width / modules

        fun isFinderArea(r: Int, c: Int) =
            (r < 7 && c < 7) || (r < 7 && c >= modules - 7) || (r >= modules - 7 && c < 7)

        // Koordinat lokal (0..6) di dalam penanda sudut 7 x 7.
        fun finderDark(r: Int, c: Int): Boolean {
            val lr = if (r >= modules - 7) r - (modules - 7) else r
            val lc = if (c >= modules - 7) c - (modules - 7) else c
            return lr == 0 || lr == 6 || lc == 0 || lc == 6 || (lr in 2..4 && lc in 2..4)
        }

        for (r in 0 until modules) {
            for (c in 0 until modules) {
                val dark = if (isFinderArea(r, c)) finderDark(r, c) else grid[r][c]
                if (dark) {
                    drawRect(
                        color = PaymentColors.QrDark,
                        topLeft = Offset(c * cell, r * cell),
                        size = Size(cell + 0.5f, cell + 0.5f), // +0,5 agar tidak ada celah antar modul
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 390x852", widthDp = 390, heightDp = 852, showBackground = true)
@Composable
private fun PaymentQrPhonePreview() = PaymentQrScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun PaymentQrSmallPreview() = PaymentQrScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun PaymentQrTabletPreview() = PaymentQrScreen()