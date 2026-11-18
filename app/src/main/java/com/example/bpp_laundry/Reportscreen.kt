package com.example.bpp_laundry

import com.example.bpp_laundry.dashboard.DashboardTab
import com.example.bpp_laundry.ui.components.AppBottomNavigation
import com.example.bpp_laundry.ui.layout.AppMainLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.bpp_laundry.ui.theme.AppColors


enum class MainTab(
    val label: String,
    val icon: ImageVector,
) {
    Beranda("Beranda", Icons.Outlined.Dashboard),
    Pesanan("Pesanan", Icons.Outlined.ListAlt),
    Pelanggan("Pelanggan", Icons.Outlined.People),
    Pengaturan("Pengaturan", Icons.Outlined.Settings),
}

/* -------------------------------------------------------------------------- */
/*  CATATAN PENTING                                                           */
/*  Metadata Figma halaman ini PARSIAL (nilai statistik, nominal, warna, dan  */
/*  tipografi bernilai null). Nilai tersebut di sini adalah ASUMSI yang       */
/*  mengikuti palet & gaya halaman Dashboard / Daftar Pesanan.                */
/*  Teks yang diasumsikan ditandai komentar "ASUMSI".                         */
/* -------------------------------------------------------------------------- */

private object ReportColors {
    val Primary = Color(0xFF0F766E)
    val OnPrimary = Color.White
    val PrimaryTint = Color(0xFFCCFBF1)
    val Background = Color(0xFFF8FAFC)   // ASUMSI: background frame null
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
    val GreenTint = Color(0xFFDCFCE7)
    val Green = Color(0xFF15803D)
}

/**
 * Font Inter. Tambahkan file font ke res/font lalu ganti, contoh:
 * FontFamily(Font(R.font.inter_regular), Font(R.font.inter_medium, FontWeight.Medium), ...)
 */
private val Inter: FontFamily = FontFamily.Default

private val CardShape = RoundedCornerShape(16.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private val ContentMaxWidth = 720.dp   // batas lebar di tablet
private val BottomBarHeight = 76.dp
private val FabSize = 56.dp
private val FabOverlap = 28.dp         // FAB menumpuk setengah tingginya di atas bar
private val ChartHeight = 164.dp       // tinggi kolom batang di desain

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

enum class ReportPeriod(val label: String) {
    Harian("Harian"),
    Mingguan("Mingguan"),
    Bulanan("Bulanan"),
}

/** [value] dalam ribu rupiah (sesuai subjudul grafik). */
data class DayRevenue(val label: String, val value: Int)

data class ReportTransaction(
    val orderId: String,
    val customer: String,
    val amount: String,
)

data class ReportUiState(
    val selectedPeriod: ReportPeriod = ReportPeriod.Mingguan, // ASUMSI: pilihan awal null
    val periodLabel: String = "28 Okt – 3 Nov 2026",           // ASUMSI: nilai null
    val totalRevenue: String = "Rp 3.240.000",
    val orderCount: String = "86",
    val chartTitle: String = "Pendapatan per Hari",
    val chartSubtitle: String = "dalam ribu rupiah",
    val bars: List<DayRevenue> = sampleBars,
    val transactionsTitle: String = "Transaksi Terbaru",       // ASUMSI: teks section header null
    val transactions: List<ReportTransaction> = sampleTransactions,
    val userInitials: String = "MA",
)

// ASUMSI: nilai grafik null; contoh ini berjumlah 3.240 (= total pendapatan).
private val sampleBars = listOf(
    DayRevenue("Rab", 420),
    DayRevenue("Kam", 380),
    DayRevenue("Jum", 510),
    DayRevenue("Sab", 640),
    DayRevenue("Min", 290),
    DayRevenue("Sen", 460),
    DayRevenue("Sel", 540),
)

// ASUMSI: hanya ID pesanan yang diketahui; nama dan nominal adalah contoh
// (LF-0248 dan LF-0247 diambil dari data Daftar Pesanan).
private val sampleTransactions = listOf(
    ReportTransaction("LF-0248", "Budi Santoso", "Rp 36.000"),
    ReportTransaction("LF-0247", "Siti Rahmawati", "Rp 28.000"),
    ReportTransaction("LF-0245", "Hendra Wijaya", "Rp 45.000"),
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */


@Composable
fun ReportScreen(
    state: ReportUiState = ReportUiState(),
    onPeriodSelected: (ReportPeriod) -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onTransactionClick: (orderId: String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
    onAddOrderClick: () -> Unit = {},
) {
    AppMainLayout(
        modifier = Modifier.background(ReportColors.Background),
        topBar = {
            Header(
                initials = state.userInitials,
                onNotificationClick = onNotificationClick,
                onAvatarClick = onAvatarClick,
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = ContentMaxWidth),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PeriodChips(
                    selected = state.selectedPeriod,
                    onSelected = onPeriodSelected,
                )

                Text(
                    text = state.periodLabel,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = ReportColors.Muted,
                    ),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        label = "Total Pendapatan",
                        value = state.totalRevenue,
                        icon = Icons.Outlined.AccountBalanceWallet,
                        iconBackground = ReportColors.GreenTint,
                        iconTint = ReportColors.Green,
                        modifier = Modifier.weight(1f),
                    )

                    StatCard(
                        label = "Jumlah Pesanan",
                        value = state.orderCount,
                        icon = Icons.Outlined.ReceiptLong,
                        iconBackground = ReportColors.PrimaryTint,
                        iconTint = ReportColors.Primary,
                        modifier = Modifier.weight(1f),
                    )
                }

                DownloadButton(
                    onClick = onDownloadClick,
                )

                RevenueChart(
                    title = state.chartTitle,
                    subtitle = state.chartSubtitle,
                    bars = state.bars,
                )

                Text(
                    text = state.transactionsTitle,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = ReportColors.Text,
                    ),
                )

                TransactionList(
                    items = state.transactions,
                    onItemClick = onTransactionClick,
                )
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedTab = DashboardTab.Laporan,
                onTabSelected = onTabSelected,
                onAddClick = onAddOrderClick,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        horizontalPadding = 20.dp,
        verticalPadding = 12.dp,
        contentGap = 12.dp,
        bottomBarHeight = 104.dp,
        bottomBarPadding = 0.dp,
    )
}


/** Membatasi lebar konten di layar lebar dan memusatkannya. */
@Composable
private fun CenteredContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .widthIn(max = ContentMaxWidth)
            .fillMaxWidth(),
    ) { content() }
}

/* -------------------------------------------------------------------------- */
/*  Header                                                                    */
/*  ASUMSI: tombol notifikasi & avatar mengikuti halaman utama lain;          */
/*  metadata hanya menyebut Header 56dp dengan judul null.                    */
/* -------------------------------------------------------------------------- */

@Composable
private fun Header(
    initials: String,
    onNotificationClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Laporan",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = ReportColors.Text,
            ),
        )

        IconButton(
            onClick = onNotificationClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Notifikasi",
                tint = AppColors.TextMain,
            ) }
    }
}

/* -------------------------------------------------------------------------- */
/*  Filter periode: chip tinggi 36, radius 18; lebar dibagi rata              */
/* -------------------------------------------------------------------------- */

@Composable
private fun PeriodChips(
    selected: ReportPeriod,
    onSelected: (ReportPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReportPeriod.values().forEach { period ->
            val isSelected = period == selected
            val shape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(shape)
                    .background(if (isSelected) ReportColors.Primary else ReportColors.Surface)
                    .then(
                        if (isSelected) Modifier else Modifier.border(1.dp, ReportColors.Border, shape),
                    )
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelected(period) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = period.label,
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) ReportColors.OnPrimary else ReportColors.Muted,
                    ),
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Kartu statistik: radius 16, padding 16, gap 8, ikon 28 (lingkaran) / 16   */
/* -------------------------------------------------------------------------- */

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .background(ReportColors.Surface)
            .border(1.dp, ReportColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = ReportColors.Muted,
            ),
        )
        Text(
            text = value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = ReportColors.Text,
            ),
        )
    }
}

/** Tombol unduh: tinggi 44, ikon download. Gaya outline teal (ASUMSI; label null). */
@Composable
private fun DownloadButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(FieldShape)
            .background(ReportColors.Surface)
            .border(1.dp, ReportColors.Primary, FieldShape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Download,
            contentDescription = null,
            tint = ReportColors.Primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Unduh Laporan",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ReportColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Grafik batang pendapatan: kartu putih, radius 16, padding 16              */
/*  Kolom batang setinggi 164dp; lebar batang maks 40dp, lebar kolom dibagi   */
/*  rata sehingga grafik menyesuaikan lebar layar. Batang diskalakan terhadap */
/*  nilai tertinggi.                                                          */
/* -------------------------------------------------------------------------- */

@Composable
private fun RevenueChart(
    title: String,
    subtitle: String,
    bars: List<DayRevenue>,
    modifier: Modifier = Modifier,
) {
    val maxValue = (bars.maxOfOrNull { it.value } ?: 0).coerceAtLeast(1)
    val summary = bars.joinToString(", ") { "${it.label} ${it.value}" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(ReportColors.Surface)
            .border(1.dp, ReportColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = ReportColors.Text,
                ),
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = ReportColors.Muted,
                ),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ChartHeight)
                .semantics { contentDescription = "$title, $subtitle: $summary" },
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            bars.forEach { bar ->
                val fraction = (bar.value.toFloat() / maxValue).coerceIn(0.04f, 1f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = bar.value.toString(),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = ReportColors.Muted,
                        ),
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(fraction)
                                .widthIn(max = 40.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(ReportColors.Primary),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = bar.label,
                        maxLines = 1,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = ReportColors.Muted,
                        ),
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Daftar transaksi: kartu putih berisi baris setinggi 56dp (lebar 319dp     */
/*  = 353 - padding 16 - border 1), dipisah divider 1dp.                      */
/* -------------------------------------------------------------------------- */

@Composable
private fun TransactionList(
    items: List<ReportTransaction>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(ReportColors.Surface)
            .border(1.dp, ReportColors.Border, CardShape)
            .padding(horizontal = 16.dp),
    ) {
        if (items.isEmpty()) {
            // State kosong belum didesain (asumsi).
            Text(
                text = "Belum ada transaksi pada periode ini.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = ReportColors.Muted,
                ),
            )
        }

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clickable(role = Role.Button) { onItemClick(item.orderId) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = item.customer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ReportColors.Text,
                        ),
                    )
                    Text(
                        text = item.orderId,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = ReportColors.Muted,
                        ),
                    )
                }
                Text(
                    text = item.amount,
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ReportColors.Primary,
                    ),
                )
            }
            if (index != items.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ReportColors.Border),
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Bottom navigation: tinggi 76, pill 56x28 (radius 14), celah tengah FAB    */
/*  Memakai MainTab dari halaman Daftar Pesanan.                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun BottomBar(
    selected: MainTab,
    onSelected: (MainTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReportColors.Surface),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ReportColors.Border),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = ContentMaxWidth)
                    .fillMaxWidth()
                    .height(BottomBarHeight - 1.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val tabs = MainTab.entries
                tabs.take(2).forEach { tab ->
                    NavItem(tab, tab == selected, { onSelected(tab) }, Modifier.weight(1f))
                }
                Spacer(Modifier.width(FabSize + 16.dp)) // celah untuk FAB
                tabs.drop(2).forEach { tab ->
                    NavItem(tab, tab == selected, { onSelected(tab) }, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    tab: MainTab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(14.dp))
            .selectable(selected = active, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (active) ReportColors.PrimaryTint else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = null,
                tint = if (active) ReportColors.Primary else ReportColors.Muted,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = tab.label,
            maxLines = 1,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 12.sp,
                color = if (active) ReportColors.Primary else ReportColors.Muted,
            ),
        )
    }
}

/** FAB: 56dp, bulat, #0F766E, bayangan seperti di halaman lain. */
@Composable
private fun AddOrderFab(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(FabSize)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = Color(0x400F1729),
                spotColor = Color(0x400F1729),
            )
            .clip(CircleShape)
            .background(ReportColors.Primary)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = "Tambah pesanan",
            tint = ReportColors.OnPrimary,
            modifier = Modifier.size(26.dp),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 393x852", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun ReportPhonePreview() = ReportScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun ReportSmallPreview() = ReportScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun ReportTabletPreview() = ReportScreen()

@Preview(name = "Tanpa transaksi", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun ReportEmptyPreview() = ReportScreen(state = ReportUiState(transactions = emptyList()))

