package com.example.laundryflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.laundryflow.components.PackageListItem
import com.example.laundryflow.components.ReorderableFormCard
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.geometry.Offset
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bpp_laundry.paketharga.PaketHargaViewModel

// ---------------------------------------------------------------------------
// Warna & tema
// Metadata Figma hanya memuat struktur & ukuran (tanpa nilai warna), jadi warna
// mengikuti token AppColors yang sudah ada di project.
// ---------------------------------------------------------------------------
private val Primary = AppColors.PrimaryApp
private val Surface0 = AppColors.MainBackground
private val CardBg = AppColors.Surface
private val Outline = AppColors.BorderApp
private val TextMain = AppColors.TextMain
private val TextSub = Color(0xFF64748B)
private val Danger = Color(0xFFDC2626)
private val DangerSoft = Color(0xFFFEE2E2)

@Composable
private fun PaketTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Primary,
            onPrimary = Color.White,
            background = Surface0,
            surface = CardBg,
            error = Danger,
        ),
        content = content
    )
}

// ---------------------------------------------------------------------------
// Model (diberi awalan Pkg agar tidak bentrok dengan file Check Point)
// ---------------------------------------------------------------------------
private data class PkgCheckPoint(val id: Int, val name: String, val colorName: String, val color: Color)

private enum class PkgIcon(val icon: ImageVector) {
    SHIRT(Icons.Default.Checkroom),
    WASHER(Icons.Default.LocalLaundryService),
    PACKAGE(Icons.Default.Inventory2),
}

private data class ServicePackage(
    val id: Int,
    val name: String,
    val pricePerKg: Int,
    val days: Int,
    val icon: PkgIcon,
    val checkPoints: List<PkgCheckPoint>,
)

// Katalog check point (di aplikasi nyata diambil dari fitur Check Point)
private val Catalog = listOf(
    PkgCheckPoint(1, "Diterima", "Abu-abu", Color(0xFF64748B)),
    PkgCheckPoint(2, "Dicuci", "Biru", Color(0xFF3B82F6)),
    PkgCheckPoint(3, "Dikeringkan", "Jingga", Color(0xFFF97316)),
    PkgCheckPoint(4, "Disetrika", "Ungu", Color(0xFF8B5CF6)),
    PkgCheckPoint(5, "Dikemas", "Hijau", Color(0xFF22C55E)),
    PkgCheckPoint(6, "Siap Diambil", "Teal", Color(0xFF14B8A6)),
)

private fun rupiah(value: Int): String =
    "Rp " + NumberFormat.getInstance(Locale("id", "ID")).format(value)

private sealed interface PkgScreen {
    data object List : PkgScreen
    data object Add : PkgScreen
    data class Edit(val item: ServicePackage) : PkgScreen
}

// ---------------------------------------------------------------------------
// Activity & root
// ---------------------------------------------------------------------------
class PaketHargaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PaketTheme { PaketHargaFeature() } }
    }
}

@Composable
fun PaketHargaFeature(
    viewModel: PaketHargaViewModel = viewModel()
) {
    val context = LocalContext.current
    val apiPackages by viewModel.packages.collectAsState()
    val catalogCps by viewModel.catalogCheckpoints.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val availableCatalog = remember(catalogCps) {
        if (catalogCps.isNotEmpty()) {
            catalogCps.map { PkgCheckPoint(it.id, it.name, it.colorName, it.color) }
        } else {
            Catalog
        }
    }

    val packages = remember(apiPackages) {
        if (apiPackages.isNotEmpty()) {
            apiPackages.map { pkg ->
                ServicePackage(
                    id = pkg.id,
                    name = pkg.name,
                    pricePerKg = pkg.pricePerKg,
                    days = pkg.days,
                    icon = PkgIcon.SHIRT,
                    checkPoints = pkg.checkPoints.map { PkgCheckPoint(it.id, it.name, it.colorName, it.color) }
                )
            }
        } else {
            emptyList()
        }
    }

    var screen by remember { mutableStateOf<PkgScreen>(PkgScreen.List) }
    var pendingDelete by remember { mutableStateOf<ServicePackage?>(null) }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    BackHandler(enabled = screen != PkgScreen.List) { screen = PkgScreen.List }

    when (val s = screen) {
        PkgScreen.List -> PackageListScreen(
            items = packages,
            isLoading = isLoading,
            onBack = { /* kembali ke layar Akun */ },
            onAdd = { screen = PkgScreen.Add },
            onEdit = { screen = PkgScreen.Edit(it) },
            onDelete = { pendingDelete = it },
        )

        PkgScreen.Add -> PackageFormScreen(
            title = "Tambah Paket",
            heading = "Detail Paket",
            hint = "Isi nama, harga per kg, estimasi kerja, dan urutan check point.",
            submitLabel = "Simpan Paket",
            initial = null,
            catalog = availableCatalog,
            isLoading = isLoading,
            onSubmit = { name, price, days, cps ->
                viewModel.createPackage(
                    name = name,
                    pricePerKg = price,
                    days = days,
                    checkpointIds = cps.map { it.id },
                    onSuccess = { screen = PkgScreen.List }
                )
            },
            onCancel = { screen = PkgScreen.List },
        )

        is PkgScreen.Edit -> PackageFormScreen(
            title = "Edit Paket",
            heading = "Ubah Paket",
            hint = "Perbarui detail paket atau atur ulang urutan check point.",
            submitLabel = "Simpan Perubahan",
            initial = s.item,
            catalog = availableCatalog,
            isLoading = isLoading,
            onSubmit = { name, price, days, cps ->
                viewModel.updatePackage(
                    id = s.item.id,
                    name = name,
                    pricePerKg = price,
                    days = days,
                    checkpointIds = cps.map { it.id },
                    onSuccess = { screen = PkgScreen.List }
                )
            },
            onCancel = { screen = PkgScreen.List },
        )
    }

    pendingDelete?.let { target ->
        DeletePackageDialog(
            item = target,
            onConfirm = {
                viewModel.deletePackage(target.id, onSuccess = {
                    pendingDelete = null
                })
            },
            onDismiss = { pendingDelete = null },
        )
    }
}

// ---------------------------------------------------------------------------
// Komponen bersama
// ---------------------------------------------------------------------------

/** Isi slot topBar milik AppMainLayout: tombol kembali + judul di tengah. */
@Composable
private fun PaketTopBar(title: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-12).dp) // sejajarkan ikon dengan tepi konten
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextMain)
        }
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextMain)
    }
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun SecondaryButton(text: String, modifier: Modifier = Modifier.fillMaxWidth(), onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Outline),
    ) { Text(text, color = TextMain, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun ServiceBadge(icon: PkgIcon, size: Int = 32) {
    Box(
        Modifier.size(size.dp).clip(RoundedCornerShape(10.dp)).background(Primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) { Icon(icon.icon, null, Modifier.size(20.dp), tint = Primary) }
}

/** Kotak pencarian untuk toolbar daftar (tinggi 40, sesuai Figma). */
@Composable
private fun SearchBox(
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CardBg)
            .border(1.dp, Outline, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, Modifier.size(16.dp), tint = TextSub)
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = TextMain),
            cursorBrush = SolidColor(Primary),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(placeholder, fontSize = 14.sp, color = TextSub)
                inner()
            }
        )
    }
}

/**
 * Input dengan label di atas (label 17 + jarak 8 + kotak 48 = 73dp, sesuai instance di Figma).
 */
@Composable
private fun LabeledInput(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
    leadingIcon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextMain)
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .background(CardBg)
                .border(1.dp, Outline, shape)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, null, Modifier.size(18.dp), tint = TextSub)
                Spacer(Modifier.width(8.dp))
            }
            if (prefix != null) {
                Text(prefix, fontSize = 14.sp, color = TextMain)
            }
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = TextMain),
                cursorBrush = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) Text(placeholder, fontSize = 14.sp, color = TextSub)
                        inner()
                    }
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Layar 1: Daftar Paket dan Harga
// ---------------------------------------------------------------------------
private enum class PackageSort(val label: String) {
    NAMA("Nama A-Z"), TERMURAH("Harga termurah"), TERMAHAL("Harga termahal"), TERCEPAT("Paling cepat")
}

@Composable
private fun PackageListScreen(
    items: List<ServicePackage>,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (ServicePackage) -> Unit,
    onDelete: (ServicePackage) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(PackageSort.NAMA) }
    var sortMenu by remember { mutableStateOf(false) }

    val shown = remember(items.toList(), query, sort) {
        items.filter { it.name.contains(query, ignoreCase = true) }.let {
            when (sort) {
                PackageSort.NAMA -> it.sortedBy { p -> p.name.lowercase() }
                PackageSort.TERMURAH -> it.sortedBy { p -> p.pricePerKg }
                PackageSort.TERMAHAL -> it.sortedByDescending { p -> p.pricePerKg }
                PackageSort.TERCEPAT -> it.sortedBy { p -> p.days }
            }
        }
    }

    // Figma: padding horizontal 20, atas 8, jarak antar blok 12.
    // AppMainLayout sudah punya verticalScroll, jadi LazyColumn diganti Column biasa.
    AppMainLayout(
        topBar = { PaketTopBar("Paket dan Harga", onBack) },
        horizontalPadding = 20.dp,
        verticalPadding = 8.dp,
        contentGap = 12.dp,
        bottomBarHeight = 0.dp, // halaman ini tidak memakai bottom bar
        content = {
            Text(
                "Kelola paket layanan, harga per kg, dan estimasi pengerjaan.",
                fontSize = 13.sp, color = TextSub
            )

            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryApp,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Tambah Paket", fontWeight = FontWeight.SemiBold)
            }

            // Toolbar: pencarian (fleksibel) + urutkan (96dp), jarak 8
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SearchBox(query, "Cari paket", { query = it }, Modifier.weight(1f))
                Box {
                    Row(
                        Modifier
                            .width(96.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBg)
                            .border(1.dp, Outline, RoundedCornerShape(10.dp))
                            .clickable { sortMenu = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.SwapVert, null, Modifier.size(16.dp), tint = TextSub)
                        Text("Urutkan", fontSize = 13.sp, color = TextMain, maxLines = 1)
                    }
                    DropdownMenu(expanded = sortMenu, onDismissRequest = { sortMenu = false }) {
                        PackageSort.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.label) },
                                onClick = { sort = mode; sortMenu = false }
                            )
                        }
                    }
                }
            }

            Text("Daftar Paket", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextMain)

            // Daftar layanan: kartu setinggi 102, jarak antar kartu 8
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (shown.isEmpty()) {
                    Text(
                        "Paket tidak ditemukan.", color = TextSub, fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    shown.forEach { p ->
                        key(p.id) {
                            PackageListItem(
                                name = p.name,
                                pricePerKg = p.pricePerKg,
                                estimatedDays = p.days,
                                onEdit = { onEdit(p) },
                                onDelete = { onDelete(p) }
                            )
                        }
                    }
                }
            }

            Text(
                "Harga di atas hanya contoh. Sesuaikan dengan tarif laundry Anda.",
                fontSize = 12.sp, color = TextSub
            )
        }
    )
}

@Composable
private fun PackageCard(item: ServicePackage, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, Outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Figma: padding 8, jarak antar baris 4 -> total tinggi 102
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                ServiceBadge(item.icon, size = 32)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextMain,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${rupiah(item.pricePerKg)} / kg", fontSize = 12.sp, color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, null, Modifier.size(14.dp), tint = TextSub)
                Spacer(Modifier.width(6.dp))
                Text("Estimasi ${item.days} hari kerja", fontSize = 12.sp, color = TextSub)
            }
            HorizontalDivider(color = Outline)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CardAction("Edit", Icons.Default.Edit, TextMain, Modifier.weight(1f), onEdit)
                Spacer(Modifier.width(12.dp))
                Box(Modifier.width(1.dp).height(24.dp).background(Outline))
                Spacer(Modifier.width(12.dp))
                CardAction("Hapus", Icons.Default.Delete, Danger, Modifier.weight(1f), onDelete)
            }
        }
    }
}

@Composable
private fun CardAction(label: String, icon: ImageVector, tint: Color, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier.height(24.dp).clip(RoundedCornerShape(6.dp)).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(16.dp), tint = tint)
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 14.sp, color = tint, fontWeight = FontWeight.Medium)
    }
}

// ---------------------------------------------------------------------------
// Layar 2 & 3: Formulir Tambah / Edit Paket
// ---------------------------------------------------------------------------
@Composable
private fun PackageFormScreen(
    title: String,
    heading: String,
    hint: String,
    submitLabel: String,
    initial: ServicePackage?,
    catalog: List<PkgCheckPoint> = Catalog,
    isLoading: Boolean = false,
    onSubmit: (name: String, price: Int, days: Int, checkPoints: List<PkgCheckPoint>) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var priceText by remember { mutableStateOf(initial?.pricePerKg?.toString() ?: "") }
    var daysText by remember { mutableStateOf(initial?.days?.toString() ?: "") }
    var selected by remember { mutableStateOf(initial?.checkPoints ?: emptyList()) }
    var cpQuery by remember { mutableStateOf("") }

    val price = priceText.toIntOrNull() ?: 0
    val days = daysText.toIntOrNull() ?: 0
    val valid = name.isNotBlank() && price > 0 && days > 0 && selected.isNotEmpty()

    val suggestions = remember(selected, cpQuery, catalog) {
        catalog.filter { c -> selected.none { it.id == c.id } && c.name.contains(cpQuery, ignoreCase = true) }
    }

    // Dipakai bersama oleh tombol panah dan drag and drop
    val move: (Int, Int) -> Unit = { from, to ->
        if (from in selected.indices && to in selected.indices) {
            selected = selected.toMutableList().apply { add(to, removeAt(from)) }
        }
    }

    // Figma: padding horizontal 20, atas 12, jarak antar blok 24, bawah 24
    // (verticalPadding 12 + bottomBarHeight 12 = 24 di bawah).
    AppMainLayout(
        topBar = { PaketTopBar(title, onCancel) },
        horizontalPadding = 20.dp,
        verticalPadding = 12.dp,
        contentGap = 24.dp,
        bottomBarHeight = 12.dp,
        content = {
            // Pengantar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(heading, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextMain)
                Text(hint, fontSize = 13.sp, color = TextSub)
            }

            // Detail paket (3 input @73dp, jarak 20)
            FormCard(spacing = 20.dp) {
                LabeledInput(
                    label = "Nama paket",
                    value = name,
                    onChange = { name = it.take(40) },
                    placeholder = "Contoh: Cuci Setrika",
                )
                LabeledInput(
                    label = "Harga (Rp per kg)",
                    value = priceText,
                    onChange = { priceText = it.filter(Char::isDigit).take(7) },
                    placeholder = "9000",
                    keyboardType = KeyboardType.Number,
                    prefix = "Rp ",
                )
                LabeledInput(
                    label = "Estimasi kerja (hari)",
                    value = daysText,
                    onChange = { daysText = it.filter(Char::isDigit).take(2) },
                    placeholder = "2",
                    keyboardType = KeyboardType.Number,
                )
            }

            // Keterangan satuan
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Info, null, Modifier.size(16.dp), tint = TextSub)
                Text(
                    "Harga dihitung per kilogram. Total tagihan = berat cucian (kg) × harga per kg.",
                    fontSize = 12.sp, color = TextSub
                )
            }

            // Pemilihan check point (jarak antar elemen 12)
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Check Point Paket", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextMain)

                // Pencarian check point
                FormCard(spacing = 12.dp) {
                    Text(
                        "Cari untuk menambahkan check point lain ke paket ini.",
                        fontSize = 13.sp, color = TextSub
                    )
                    LabeledInput(
                        label = "Cari check point",
                        value = cpQuery,
                        onChange = { cpQuery = it },
                        placeholder = "Cari check point",
                        leadingIcon = Icons.Default.Search,
                    )
                    if (suggestions.isEmpty()) {
                        Text("Tidak ada check point lain yang tersedia.", fontSize = 12.sp, color = TextSub)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            suggestions.forEach { c ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selected = selected + c; cpQuery = "" }
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.size(12.dp).clip(CircleShape).background(c.color))
                                    Spacer(Modifier.width(10.dp))
                                    Text(c.name, fontSize = 14.sp, color = TextMain, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.Add, "Tambah ${c.name}", Modifier.size(18.dp), tint = Primary)
                                }
                            }
                        }
                    }
                }

                // Check point dipilih: tahan lalu geser baris untuk drag and drop,
                // atau pakai tombol panah.
                ReorderableFormCard(
                    items = selected,
                    itemKey = { it.id },
                    onMove = move,
                    contentSpacing = 12.dp,
                    itemSpacing = 10.dp,
                    longPressToDrag = true,
                    containerColor = CardBg,
                    borderColor = Outline,
                    header = { SelectedCheckPointHeader(count = selected.size) },
                    emptyContent = { EmptyCheckPointHint() },
                ) { cp, index, isDragging, dragHandle ->
                    SelectedCheckPointRow(
                        order = index + 1,
                        cp = cp,
                        isDragging = isDragging,
                        dragHandle = dragHandle,
                        canUp = index > 0,
                        canDown = index < selected.lastIndex,
                        onUp = { move(index, index - 1) },
                        onDown = { move(index, index + 1) },
                        onRemove = { selected = selected.filter { it.id != cp.id } },
                    )
                }
            }

            // Aksi formulir (jarak 10)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton(if (isLoading) "Menyimpan..." else submitLabel, enabled = valid && !isLoading) { onSubmit(name.trim(), price, days, selected) }
                SecondaryButton("Batal", onClick = onCancel)
            }
        }
    )
}

@Composable
private fun FormCard(
    spacing: androidx.compose.ui.unit.Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, Outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content
        )
    }
}

// ---------------------------------------------------------------------------
// Isi kartu "Check point dipilih"
// ---------------------------------------------------------------------------
@Composable
private fun SelectedCheckPointHeader(count: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Check point dipilih", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextMain)
        Text("$count dipilih", fontSize = 12.sp, color = TextSub)
    }
}

@Composable
private fun EmptyCheckPointHint() {
    Text("Belum ada check point. Pilih minimal satu.", fontSize = 13.sp, color = TextSub)
}

/**
 * Baris check point terpilih. Tata letak mengikuti Figma (tinggi 78):
 * nomor 28 · penanda warna 40 · info (fleksibel) · pengatur urutan 32x54 · hapus 32,
 * dengan jarak 12 dan padding 12.
 * [dragHandle] dipasang di seluruh baris (tahan lama lalu geser).
 */
@Composable
private fun SelectedCheckPointRow(
    order: Int,
    cp: PkgCheckPoint,
    isDragging: Boolean,
    dragHandle: Modifier,
    canUp: Boolean,
    canDown: Boolean,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onRemove: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .then(dragHandle)
            .clip(shape)
            .background(if (isDragging) CardBg else Surface0)
            .border(1.dp, if (isDragging) Primary else Outline, shape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Nomor urut
        Box(
            Modifier.size(28.dp).clip(CircleShape).background(Primary),
            contentAlignment = Alignment.Center
        ) { Text("$order", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }

        // Penanda warna
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(cp.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Box(Modifier.size(18.dp).clip(CircleShape).background(cp.color)) }

        // Informasi
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                cp.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextMain,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text("Warna: ${cp.colorName}", fontSize = 12.sp, color = TextSub)
        }

        // Pengatur urutan (32 x 54)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MoveButton(Icons.Default.KeyboardArrowUp, "Naikkan", canUp, onUp)
            MoveButton(Icons.Default.KeyboardArrowDown, "Turunkan", canDown, onDown)
        }

        // Hapus
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, "Hapus ${cp.name}", Modifier.size(16.dp), tint = Danger)
        }
    }
}

@Composable
private fun MoveButton(icon: ImageVector, desc: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(width = 32.dp, height = 24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(CardBg)
            .border(1.dp, Outline, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, desc, Modifier.size(14.dp), tint = if (enabled) TextMain else Outline)
    }
}

// ---------------------------------------------------------------------------
// Layar 4: Dialog Konfirmasi Hapus Paket (dialog, bukan halaman, jadi tidak pakai AppMainLayout)
// ---------------------------------------------------------------------------
@Composable
private fun DeletePackageDialog(item: ServicePackage, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = CardBg) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(DangerSoft),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Delete, null, Modifier.size(22.dp), tint = Danger) }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hapus paket ini?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextMain)
                    Text(
                        "Paket akan dihapus dari daftar layanan dan tidak bisa dipilih lagi untuk pesanan baru. Tindakan ini tidak dapat dibatalkan.",
                        fontSize = 14.sp, color = TextSub
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface0)
                        .border(1.dp, Outline, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextMain)
                    Text(
                        "${rupiah(item.pricePerKg)} / kg · ${item.days} hari",
                        fontSize = 13.sp, color = TextSub
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryButton("Batal", Modifier.weight(1f), onDismiss)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Danger, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) { Text("Hapus Paket", fontWeight = FontWeight.SemiBold, maxLines = 1) }
                }
            }
        }
    }
}