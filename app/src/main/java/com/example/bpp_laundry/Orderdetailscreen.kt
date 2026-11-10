package com.example.bpp_laundry



import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.bpp_laundry.dashboard.*

/* -------------------------------------------------------------------------- */
/*  CATATAN PENTING                                                           */
/*  Metadata Figma halaman ini PARSIAL: hanya nama & telepon pelanggan yang   */
/*  diketahui. Nilai pesanan lain, warna, dan tipografi bernilai null dan di  */
/*  sini adalah ASUMSI yang mengikuti palet & gaya halaman Dashboard / Daftar */
/*  Pesanan. Teks yang diasumsikan ditandai komentar "ASUMSI".                */
/* -------------------------------------------------------------------------- */

private object DetailColors {
    val Primary = Color(0xFF0F766E)
    val OnPrimary = Color.White
    val PrimaryTint = Color(0xFFCCFBF1)
    val Background = Color(0xFFF8FAFC)   // ASUMSI: background frame null
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
}

/**
 * Font Inter. Tambahkan file font ke res/font lalu ganti, contoh:
 * FontFamily(Font(R.font.inter_regular), Font(R.font.inter_medium, FontWeight.Medium), ...)
 */
private val Inter: FontFamily = FontFamily.Default

private val CardShape = RoundedCornerShape(16.dp)
private val ButtonShape = RoundedCornerShape(12.dp)
private val ContentMaxWidth = 560.dp   // batas lebar di tablet

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

/** ASUMSI: nilai badge pembayaran null (lebar 106dp di desain cocok dengan "Belum Lunas"). */
enum class PaymentStatus(val label: String, val background: Color, val content: Color) {
    Lunas("Lunas", Color(0xFFDCFCE7), Color(0xFF15803D)),
    BelumLunas("Belum Lunas", Color(0xFFFEF3C7), Color(0xFFB45309)),
}

data class SummaryRow(val label: String, val value: String)

data class OrderDetailUiState(
    val orderId: String = "LF-0250",
    val status: OrderStatus = OrderStatus.Dicuci,
    val service: String = "Cuci + Setrika  •  4,0 kg",
    val receivedDate: String = "8 Nov 2026",
    val estimateDate: String = "10 Nov 2026",
    val customerName: String = "Andi Pratama",
    val customerPhone: String = "0852-7766-3300",
    val paymentStatus: PaymentStatus = PaymentStatus.BelumLunas,
    val summaryRows: List<SummaryRow> = listOf(
        SummaryRow("Cuci + Setrika (4,0 kg × Rp 8.000)", "Rp 32.000"),
        SummaryRow("Biaya antar jemput", "Rp 0"),
    ),
    val total: String = "Rp 32.000",
    /** Keterangan waktu tiap tahap yang sudah dilalui (opsional). */
    val stepTimes: Map<OrderStatus, String> = mapOf(
        OrderStatus.Diterima to "8 Nov 2026, 09.15",
        OrderStatus.Dicuci to "9 Nov 2026, 08.30",
    ),
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun OrderDetailScreen(
    state: OrderDetailUiState = OrderDetailUiState(),
    onBackClick: () -> Unit = {},          // -> 02 - Daftar Pesanan (1:2614, asumsi)
    onChangeStatusClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailColors.Background)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DetailColors.Surface)
                            .border(1.dp, DetailColors.Border, CircleShape)
                            .clickable(role = Role.Button, onClick = onBackClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DetailColors.Text,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    // ASUMSI: judul header null di metadata.
                    Text(
                        text = "Detail Pesanan",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            color = DetailColors.Text,
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OrderInfoCard(state)

                    // Rincian pembayaran: judul + badge, lalu kartu ringkasan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Rincian Pembayaran",
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = DetailColors.Text,
                            ),
                        )
                        PillBadge(
                            text = state.paymentStatus.label,
                            background = state.paymentStatus.background,
                            content = state.paymentStatus.content,
                        )
                    }
                    PaymentSummaryCard(rows = state.summaryRows, total = state.total)

                    ProgressCard(current = state.status, stepTimes = state.stepTimes)
                }

                // ----- Aksi (tinggi 48): Ubah Status + Simpan Pesanan -----
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DetailColors.Background)
                        .navigationBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // ASUMSI: label tombol null; memakai nama layer.
                    ActionButton(
                        text = "Ubah Status",
                        primary = false,
                        onClick = onChangeStatusClick,
                        modifier = Modifier.weight(1f),
                    )
                    ActionButton(
                        text = "Simpan Pesanan",
                        primary = true,
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Info pesanan: ID, badge status, layanan, tanggal, pelanggan               */
/*  Tata letak di dalam kartu adalah perkiraan (metadata hanya daftar elemen).*/
/* -------------------------------------------------------------------------- */

@Composable
private fun OrderInfoCard(state: OrderDetailUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(DetailColors.Surface)
            .border(1.dp, DetailColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = state.orderId,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = DetailColors.Text,
                ),
            )
            PillBadge(
                text = state.status.label,
                background = state.status.background,
                content = state.status.content,
                showDot = true,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = state.service,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = DetailColors.Text,
                ),
            )
            // ASUMSI: format tanggal; nilai null di metadata.
            Text(
                text = "Diterima ${state.receivedDate}  •  Estimasi ${state.estimateDate}",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = DetailColors.Muted,
                ),
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DetailColors.Border),
        )

        // Pelanggan: avatar 40dp (inisial), nama, telepon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DetailColors.PrimaryTint),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initialsOf(state.customerName),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = DetailColors.Primary,
                    ),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = state.customerName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = DetailColors.Text,
                    ),
                )
                Text(
                    text = state.customerPhone,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = DetailColors.Muted,
                    ),
                )
            }
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

/** Badge pil: radius 12, padding 10x4 (tinggi sekitar 23dp seperti di desain). */
@Composable
private fun PillBadge(
    text: String,
    background: Color,
    content: Color,
    showDot: Boolean = false,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (showDot) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(content),
            )
        }
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = content,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Ringkasan pembayaran: baris rincian, divider, total                       */
/* -------------------------------------------------------------------------- */

@Composable
private fun PaymentSummaryCard(rows: List<SummaryRow>, total: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(DetailColors.Surface)
            .border(1.dp, DetailColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = row.label,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = DetailColors.Muted,
                    ),
                )
                Text(
                    text = row.value,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = DetailColors.Text,
                    ),
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DetailColors.Border),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Total",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DetailColors.Text,
                ),
            )
            Text(
                text = total,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DetailColors.Primary,
                ),
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Progres pengerjaan: 5 tahap vertikal                                      */
/*  Diterima -> Dicuci -> Disetrika -> Siap Diambil -> Selesai                */
/*  Sudah dilalui: lingkaran teal + centang. Saat ini: cincin teal + titik.   */
/*  Belum: cincin abu-abu. Tahap saat ini di desain null (ASUMSI: mengikuti   */
/*  status pesanan).                                                          */
/* -------------------------------------------------------------------------- */

@Composable
private fun ProgressCard(
    current: OrderStatus,
    stepTimes: Map<OrderStatus, String>,
) {
    val steps = OrderStatus.entries
    val currentIndex = steps.indexOf(current)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(DetailColors.Surface)
            .border(1.dp, DetailColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Progres Pengerjaan",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = DetailColors.Text,
            ),
        )

        Column(
            modifier = Modifier.semantics {
                contentDescription = "Progres pengerjaan, tahap saat ini: ${current.label}"
            },
        ) {
            steps.forEachIndexed { index, step ->
                val isDone = index < currentIndex
                val isCurrent = index == currentIndex
                val isLast = index == steps.lastIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Penanda + garis penghubung
                    Column(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        StepMarker(done = isDone, current = isCurrent)
                        if (!isLast) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .width(2.dp)
                                    .background(if (isDone) DetailColors.Primary else DetailColors.Border),
                            )
                        }
                    }

                    // Label + keterangan waktu
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (isLast) 0.dp else 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = step.label,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = if (isDone || isCurrent) FontWeight.SemiBold else FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 24.sp, // sejajar dengan penanda 24dp
                                color = if (isDone || isCurrent) DetailColors.Text else DetailColors.Muted,
                            ),
                        )
                        val time = stepTimes[step]
                        if (time != null && (isDone || isCurrent)) {
                            Text(
                                text = time,
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = DetailColors.Muted,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepMarker(done: Boolean, current: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .then(
                when {
                    done -> Modifier.background(DetailColors.Primary)
                    current -> Modifier
                        .background(DetailColors.Surface)
                        .border(2.dp, DetailColors.Primary, CircleShape)
                    else -> Modifier
                        .background(DetailColors.Surface)
                        .border(1.5.dp, DetailColors.Border, CircleShape)
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            done -> Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = DetailColors.OnPrimary,
                modifier = Modifier.size(14.dp),
            )
            current -> Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(DetailColors.Primary),
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Tombol aksi: tinggi 48, radius 12, lebar dibagi rata (172 / 169 di desain)*/
/* -------------------------------------------------------------------------- */

@Composable
private fun ActionButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(ButtonShape)
            .background(if (primary) DetailColors.Primary else DetailColors.Surface)
            .then(
                if (primary) Modifier else Modifier.border(1.5.dp, DetailColors.Primary, ButtonShape),
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (primary) DetailColors.OnPrimary else DetailColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 393x852", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun OrderDetailPhonePreview() = OrderDetailScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun OrderDetailSmallPreview() = OrderDetailScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun OrderDetailTabletPreview() = OrderDetailScreen()
