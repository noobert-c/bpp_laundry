package com.example.bpp_laundry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* -------------------------------------------------------------------------- */
/*  CATATAN PENTING                                                           */
/*  Metadata Figma halaman ini PARSIAL (teks, warna, tipografi bernilai       */
/*  null). Nilai tersebut di sini adalah ASUMSI yang mengikuti palet & gaya   */
/*  halaman Dashboard / Daftar Pesanan (teal #0F766E, slate).                 */
/*  Teks yang diasumsikan ditandai komentar "ASUMSI".                         */
/* -------------------------------------------------------------------------- */

private object ShuttleColors {
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
private val FieldShape = RoundedCornerShape(12.dp)
private val ContentMaxWidth = 720.dp   // batas lebar di tablet

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

/** ASUMSI: warna badge jemput/antar tidak ada di metadata. */
enum class ShuttleType(val label: String, val background: Color, val content: Color) {
    Jemput("Jemput", Color(0xFFDBEAFE), Color(0xFF1D4ED8)),
    Antar("Antar", Color(0xFFCCFBF1), Color(0xFF0F766E)),
}

enum class ShuttleFilter(val label: String) {
    Semua("Semua tujuan"),
    Jemput("Tujuan jemput"),
    Antar("Tujuan antar"),
}

data class ShuttleDestination(
    val id: String,
    val customerName: String,
    val type: ShuttleType,
    val distance: String,            // sudah diformat, mis. "1,2 km"
    val noteTitle: String = "Catatan pelanggan",
    val description: String? = null, // hanya ditampilkan bila ada (di desain: kartu pertama)
    val landmark: String,
    val orderId: String,
)

/**
 * [destinations] diharapkan sudah difilter oleh ViewModel sesuai [selectedFilter]
 * dan diurutkan (mis. dari yang terdekat).
 */
data class ShuttleUiState(
    val currentLocationLabel: String = "Lokasi saat ini", // ASUMSI: label_lokasi null
    val selectedFilter: ShuttleFilter = ShuttleFilter.Semua,
    val destinations: List<ShuttleDestination> = sampleDestinations,
    val userInitials: String = "MA",
)

// ASUMSI: seluruh isi kartu (nama, jarak, catatan, patokan) adalah data contoh.
// Nama pelanggan dan ID pesanan diambil dari data Daftar Pesanan.
private val sampleDestinations = listOf(
    ShuttleDestination(
        id = "s1",
        customerName = "Rina Kartika",
        type = ShuttleType.Jemput,
        distance = "1,2 km",
        description = "Pelanggan langganan. Mohon hubungi sebelum tiba.",
        landmark = "Depan minimarket, pagar hijau.",
        orderId = "LF-0251",
    ),
    ShuttleDestination(
        id = "s2",
        customerName = "Andi Pratama",
        type = ShuttleType.Antar,
        distance = "2,4 km",
        landmark = "Gang Melati No. 5, dekat masjid.",
        orderId = "LF-0250",
    ),
    ShuttleDestination(
        id = "s3",
        customerName = "Dewi Lestari",
        type = ShuttleType.Jemput,
        distance = "3,1 km",
        landmark = "Perumahan Griya Asri, blok C.",
        orderId = "LF-0249",
    ),
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@Composable
fun ShuttleScreen(
    state: ShuttleUiState = ShuttleUiState(),
    onBackClick: () -> Unit = {},                          // -> 02 - Daftar Pesanan (1:2614, asumsi)
    onFilterSelected: (ShuttleFilter) -> Unit = {},
    onOpenRouteClick: (destinationId: String) -> Unit = {}, // mis. buka aplikasi peta dengan rute ke tujuan
    onOpenOrderClick: (orderId: String) -> Unit = {},      // -> 04 - Detail Pesanan (1:1355, asumsi)
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    initialExpandedId: String? = state.destinations.firstOrNull()?.id, // di desain: kartu pertama terbuka
) {
    // Hanya satu kartu terbuka dalam satu waktu (ASUMSI: perilaku akordeon).
    var expandedId by rememberSaveable { mutableStateOf(initialExpandedId) }

    val jemputCount = state.destinations.count { it.type == ShuttleType.Jemput }
    val antarCount = state.destinations.count { it.type == ShuttleType.Antar }
    val summary = "${state.destinations.size} tujuan • $jemputCount jemput • $antarCount antar" // ASUMSI

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShuttleColors.Background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Box(Modifier.statusBarsPadding()) {
                    CenteredContent {
                        Header(
                            initials = state.userInitials,
                            onBackClick = onBackClick,
                            onNotificationClick = onNotificationClick,
                            onAvatarClick = onAvatarClick,
                        )
                    }
                }
            }

            item {
                CenteredContent {
                    CurrentLocationBar(
                        label = state.currentLocationLabel,
                        summary = summary,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            }

            item {
                CenteredContent {
                    FilterChips(selected = state.selectedFilter, onSelected = onFilterSelected)
                }
            }

            if (state.destinations.isEmpty()) {
                // State kosong belum didesain (asumsi).
                item {
                    Text(
                        text = "Belum ada tujuan antar jemput.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = ShuttleColors.Muted,
                        ),
                    )
                }
            } else {
                items(state.destinations, key = { it.id }) { destination ->
                    CenteredContent {
                        DestinationCard(
                            destination = destination,
                            expanded = expandedId == destination.id,
                            onToggle = {
                                expandedId = if (expandedId == destination.id) null else destination.id
                            },
                            onOpenRouteClick = { onOpenRouteClick(destination.id) },
                            onOpenOrderClick = { onOpenOrderClick(destination.orderId) },
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                }
            }

            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
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
/*  Header: tombol kembali + judul + notifikasi + avatar (tinggi 56)          */
/* -------------------------------------------------------------------------- */

@Composable
private fun Header(
    initials: String,
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
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
                .background(ShuttleColors.Surface)
                .border(1.dp, ShuttleColors.Border, CircleShape)
                .clickable(role = Role.Button, onClick = onBackClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Kembali ke pesanan",
                tint = ShuttleColors.Text,
                modifier = Modifier.size(20.dp),
            )
        }

        // ASUMSI: judul null di metadata.
        Text(
            text = "Antar Jemput",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = ShuttleColors.Text,
            ),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ShuttleColors.Surface)
                    .border(1.dp, ShuttleColors.Border, CircleShape)
                    .clickable(role = Role.Button, onClick = onNotificationClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = ShuttleColors.Text,
                    modifier = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ShuttleColors.PrimaryTint)
                    .clickable(role = Role.Button, onClick = onAvatarClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ShuttleColors.Primary,
                    ),
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Titik saat ini: tinggi 44, ikon locate-fixed, label + ringkasan tujuan    */
/* -------------------------------------------------------------------------- */

@Composable
private fun CurrentLocationBar(
    label: String,
    summary: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(FieldShape)
            .background(ShuttleColors.PrimaryTint)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.MyLocation, // locate-fixed
            contentDescription = null,
            tint = ShuttleColors.Primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ShuttleColors.Primary,
            ),
        )
        Text(
            text = summary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 180.dp),
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = ShuttleColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Filter tugas: chip tinggi 36, radius 18; bisa di-scroll horizontal        */
/* -------------------------------------------------------------------------- */

@Composable
private fun FilterChips(
    selected: ShuttleFilter,
    onSelected: (ShuttleFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ShuttleFilter.entries.toList()) { filter ->
            val isSelected = filter == selected
            val shape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(shape)
                    .background(if (isSelected) ShuttleColors.Primary else ShuttleColors.Surface)
                    .then(
                        if (isSelected) Modifier else Modifier.border(1.dp, ShuttleColors.Border, shape),
                    )
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelected(filter) },
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = filter.label,
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) ShuttleColors.OnPrimary else ShuttleColors.Muted,
                    ),
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Kartu tujuan (terbuka / tertutup): radius 16, padding 16                  */
/*  Tertutup: nama, badge jenis, jarak, aksi rute, judul catatan (dropdown).  */
/*  Terbuka : + deskripsi pelanggan (bila ada), patokan, tombol buka pesanan. */
/*  Tata letak di dalam kartu adalah perkiraan; metadata hanya daftar elemen. */
/* -------------------------------------------------------------------------- */

@Composable
private fun DestinationCard(
    destination: ShuttleDestination,
    expanded: Boolean,
    onToggle: () -> Unit,
    onOpenRouteClick: () -> Unit,
    onOpenOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(ShuttleColors.Surface)
            .border(1.dp, ShuttleColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Baris atas: nama + jarak (kiri), badge + aksi rute (kanan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = destination.customerName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = ShuttleColors.Text,
                    ),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place, // map-pin
                        contentDescription = null,
                        tint = ShuttleColors.Muted,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = destination.distance,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = ShuttleColors.Muted,
                        ),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                TypeBadge(destination.type)
                RouteAction(
                    customerName = destination.customerName,
                    onClick = onOpenRouteClick,
                )
            }
        }

        // Dropdown catatan: ikon message-square-text + judul + panah
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .semantics { stateDescription = if (expanded) "Terbuka" else "Tertutup" }
                .clickable(role = Role.Button, onClick = onToggle)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Notes, // message-square-text
                contentDescription = null,
                tint = ShuttleColors.Muted,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = destination.noteTitle,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = ShuttleColors.Text,
                ),
            )
            Icon(
                imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (expanded) "Tutup catatan" else "Buka catatan",
                tint = ShuttleColors.Muted,
                modifier = Modifier.size(20.dp),
            )
        }

        // Isi dropdown (tersembunyi saat tertutup)
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ShuttleColors.Border),
                )

                if (destination.description != null) {
                    Text(
                        text = destination.description,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = ShuttleColors.Muted,
                        ),
                    )
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = ShuttleColors.Text)) {
                            append("Patokan: ") // ASUMSI: label
                        }
                        append(destination.landmark)
                    },
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = ShuttleColors.Muted,
                    ),
                )

                OpenOrderButton(onClick = onOpenOrderClick)
            }
        }
    }
}

/** Badge jenis tugas (jemput / antar): radius 12, padding 10x4. */
@Composable
private fun TypeBadge(type: ShuttleType) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(type.background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = type.label,
            maxLines = 1,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = type.content,
            ),
        )
    }
}

/** Aksi "Buka rute" dengan chevron-right. */
@Composable
private fun RouteAction(customerName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClickLabel = "Buka rute ke $customerName", role = Role.Button, onClick = onClick)
            .padding(start = 4.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Buka rute", // ASUMSI: label dari nama layer
            maxLines = 1,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = ShuttleColors.Primary,
            ),
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = ShuttleColors.Primary,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** Tombol "Buka pesanan": label selebar 96dp di desain; tinggi 36 (ASUMSI). */
@Composable
private fun OpenOrderButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ShuttleColors.Surface)
            .border(1.dp, ShuttleColors.Primary, RoundedCornerShape(10.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Buka pesanan",
            modifier = Modifier.widthIn(min = 96.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = ShuttleColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 393x852", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun ShuttlePhonePreview() = ShuttleScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun ShuttleSmallPreview() = ShuttleScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun ShuttleTabletPreview() = ShuttleScreen()

@Preview(name = "Kosong", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun ShuttleEmptyPreview() = ShuttleScreen(state = ShuttleUiState(destinations = emptyList()))