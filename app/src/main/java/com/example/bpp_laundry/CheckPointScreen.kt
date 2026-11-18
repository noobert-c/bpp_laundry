package com.example.laundryflow

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bpp_laundry.checkpoint.CheckPointViewModel
import com.example.bpp_laundry.data.model.ColorResponse
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.laundryflow.components.CheckPoint
import com.example.laundryflow.components.CheckPointCard
import com.example.laundryflow.components.CheckPointIdentity

// ---------------------------------------------------------------------------
// Warna & tema
// ---------------------------------------------------------------------------
private val Primary = Color(0xFF2563EB)
private val OnPrimary = Color.White
private val Surface0 = Color(0xFFF6F8FB)
private val CardBg = Color.White
private val Outline = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextSub = Color(0xFF64748B)
private val Danger = Color(0xFFDC2626)
private val DangerSoft = Color(0xFFFEE2E2)

// Palet warna cadangan jika API belum aktif / data kosong
private val FallbackColors = listOf(
    ColorResponse(1, "Merah", "0xFFEF4444"),
    ColorResponse(2, "Oranye", "0xFFF97316"),
    ColorResponse(3, "Kuning Tua", "0xFFF59E0B"),
    ColorResponse(4, "Kuning", "0xFFEAB308"),
    ColorResponse(5, "Lime", "0xFF84CC16"),
    ColorResponse(6, "Hijau", "0xFF22C55E"),
    ColorResponse(7, "Emerald", "0xFF10B981"),
    ColorResponse(8, "Teal", "0xFF14B8A6"),
    ColorResponse(9, "Cyan", "0xFF06B6D4"),
    ColorResponse(10, "Sky", "0xFF0EA5E9"),
    ColorResponse(11, "Biru", "0xFF3B82F6"),
    ColorResponse(12, "Indigo", "0xFF6366F1"),
    ColorResponse(13, "Ungu", "0xFF8B5CF6"),
    ColorResponse(14, "Purple", "0xFFA855F7"),
    ColorResponse(15, "Fuchsia", "0xFFD946EF"),
    ColorResponse(16, "Pink", "0xFFEC4899"),
    ColorResponse(17, "Rose", "0xFFF43F5E"),
    ColorResponse(18, "Merah Tua", "0xFFB91C1C"),
    ColorResponse(19, "Cokelat", "0xFF92400E"),
    ColorResponse(20, "Abu Hangat", "0xFF78716C"),
    ColorResponse(21, "Slate", "0xFF64748B"),
    ColorResponse(22, "Abu Tua", "0xFF475569"),
    ColorResponse(23, "Gelap", "0xFF1E293B"),
    ColorResponse(24, "Teal Tua", "0xFF0F766E")
)

@Composable
private fun LaundryFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Primary,
            onPrimary = OnPrimary,
            background = Surface0,
            surface = CardBg,
            error = Danger,
        ),
        content = content
    )
}

// ---------------------------------------------------------------------------
// Navigasi sederhana
// ---------------------------------------------------------------------------
private sealed interface Screen {
    data object List : Screen
    data object Add : Screen
    data class Edit(val item: CheckPoint) : Screen
}

@Composable
fun CheckPointScreen(
    viewModel: CheckPointViewModel = viewModel()
) {
    val context = LocalContext.current
    val checkpointList by viewModel.checkpoints.collectAsState()
    val apiColors by viewModel.colors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val colorsList = remember(apiColors) {
        if (apiColors.isNotEmpty()) apiColors else FallbackColors
    }

    // Konversi CheckPointUiModel ke CheckPoint komponen
    val items = remember(checkpointList) {
        if (checkpointList.isNotEmpty()) {
            checkpointList.map { CheckPoint(it.id, it.name, it.color) }
        } else {
            // Data awal default jika server belum terisi
            listOf(
                CheckPoint(1, "Diterima", Color(0xFF3B82F6)),
                CheckPoint(2, "Dicuci", Color(0xFF22C55E)),
                CheckPoint(3, "Dikeringkan", Color(0xFFF97316)),
                CheckPoint(4, "Disetrika", Color(0xFF8B5CF6)),
            )
        }
    }

    var screen by remember { mutableStateOf<Screen>(Screen.List) }
    var pendingDelete by remember { mutableStateOf<CheckPoint?>(null) }

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

    BackHandler(enabled = screen != Screen.List) { screen = Screen.List }

    when (val s = screen) {
        Screen.List -> CheckPointListScreen(
            items = items,
            onBack = { /* kembali ke layar Akun */ },
            onAdd = { screen = Screen.Add },
            onEdit = { screen = Screen.Edit(it) },
            onDelete = { pendingDelete = it },
        )

        Screen.Add -> {
            // Hanya tampilkan warna yang BELUM digunakan (is_used == false)
            val availableColorsForAdd = remember(colorsList) {
                colorsList.filter { !it.isUsed }
            }
            CheckPointFormScreen(
                title = "Tambah Check Point",
                heading = "Detail Check Point",
                hint = "Beri nama dan pilih warna untuk titik progres baru.",
                submitLabel = "Simpan Check Point",
                initial = null,
                availableColors = availableColorsForAdd,
                isLoading = isLoading,
                onSubmit = { name, colorId ->
                    viewModel.createCheckpoint(
                        name = name,
                        colorId = colorId,
                        onSuccess = { screen = Screen.List }
                    )
                },
                onCancel = { screen = Screen.List },
            )
        }

        is Screen.Edit -> {
            val currentCp = checkpointList.firstOrNull { it.id == s.item.id }
            // Tampilkan warna yang belum digunakan PLUS warna saat ini yang sedang dipakai checkpoint ini
            val availableColorsForEdit = remember(colorsList, currentCp) {
                colorsList.filter { !it.isUsed || it.id == currentCp?.colorId }
            }
            CheckPointFormScreen(
                title = "Edit Check Point",
                heading = "Ubah Check Point",
                hint = "Perbarui nama atau warna check point ini.",
                submitLabel = "Simpan Perubahan",
                initial = s.item,
                initialColorId = currentCp?.colorId,
                availableColors = availableColorsForEdit,
                isLoading = isLoading,
                onSubmit = { name, colorId ->
                    viewModel.updateCheckpoint(
                        id = s.item.id,
                        name = name,
                        colorId = colorId,
                        oldColorId = currentCp?.colorId,
                        onSuccess = { screen = Screen.List }
                    )
                },
                onCancel = { screen = Screen.List },
            )
        }
    }

    pendingDelete?.let { target ->
        DeleteConfirmDialog(
            item = target,
            onConfirm = {
                val colorId = checkpointList.firstOrNull { it.id == target.id }?.colorId
                viewModel.deleteCheckpoint(target.id, colorId)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null },
        )
    }
}

// ---------------------------------------------------------------------------
// Komponen bersama
// ---------------------------------------------------------------------------

@Composable
private fun CheckPointTopBar(title: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-12).dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = TextMain
            )
        }
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextMain)
    }
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun SecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Outline),
    ) { Text(text, color = TextMain, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun ColorPalette(
    colors: List<ColorResponse>,
    selectedColorId: Int,
    onSelect: (ColorResponse) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.chunked(6).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { c ->
                    val composeColor = c.toComposeColor()
                    val isSelected = c.id == selectedColorId
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(composeColor)
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) TextMain else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onSelect(c) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Dipilih",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
                repeat(6 - row.size) {
                    Spacer(Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Layar 1: Daftar Check Point
// ---------------------------------------------------------------------------
private enum class SortMode(val label: String) { AZ("Nama A-Z"), ZA("Nama Z-A"), BARU("Terbaru") }

@Composable
private fun CheckPointListScreen(
    items: List<CheckPoint>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (CheckPoint) -> Unit,
    onDelete: (CheckPoint) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf(SortMode.BARU) }
    var sortExpanded by remember { mutableStateOf(false) }

    val filtered = remember(items, query) {
        if (query.isBlank()) items
        else items.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }

    val shown = remember(filtered, sortMode) {
        when (sortMode) {
            SortMode.AZ -> filtered.sortedBy { it.name.lowercase() }
            SortMode.ZA -> filtered.sortedByDescending { it.name.lowercase() }
            SortMode.BARU -> filtered.sortedByDescending { it.id }
        }
    }

    AppMainLayout(
        topBar = { CheckPointTopBar("Kelola Check Point", onBack) },
        contentGap = 16.dp,
        bottomBarHeight = 0.dp,
        content = {
            // Pencarian
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Cari check point...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSub) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBg,
                    focusedContainerColor = CardBg,
                    unfocusedBorderColor = Outline,
                    focusedBorderColor = Primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // Baris filter & tombol tambah
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Outline),
                        color = CardBg,
                        // clip sebelum clickable agar efek ripple mengikuti sudut membulat
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { sortExpanded = true }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sortMode.label, fontSize = 13.sp, color = TextMain, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(16.dp), tint = TextSub)
                        }
                    }

                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        containerColor = CardBg
                    ) {
                        SortMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.label, fontSize = 13.sp) },
                                onClick = {
                                    sortMode = mode
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tambah", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Kartu ringkasan total
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Outline),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Total Check Point", fontSize = 13.sp, color = TextSub)
                        Text(
                            "${items.size} titik",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextMain
                        )
                    }
                    Text(
                        "Urutan progres pesanan",
                        fontSize = 12.sp,
                        color = TextSub,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Judul daftar
            Text(
                "Daftar Check Point",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextMain
            )

            // Daftar item (jarak antar kartu lebih rapat dari jarak antar section)
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (shown.isEmpty()) {
                    Text(
                        "Check point tidak ditemukan.", color = TextSub, fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    shown.forEach { cp ->
                        key(cp.id) {
                            CheckPointCard(
                                item = cp,
                                onEdit = { selected -> onEdit(selected) },
                                onDelete = { selected -> onDelete(selected) }
                            )
                        }
                    }
                }
            }

            Text(
                "Warna check point membantu pelanggan mengenali tahap progres cucian mereka dengan cepat.",
                fontSize = 12.sp, color = TextSub
            )
        }
    )
}

// ---------------------------------------------------------------------------
// Layar 2 & 3: Formulir Tambah / Edit
// ---------------------------------------------------------------------------
@Composable
private fun CheckPointFormScreen(
    title: String,
    heading: String,
    hint: String,
    submitLabel: String,
    initial: CheckPoint?,
    initialColorId: Int? = null,
    availableColors: List<ColorResponse>,
    isLoading: Boolean = false,
    onSubmit: (String, Int) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var selectedColorId by remember(availableColors) {
        val foundId = initialColorId
            ?: availableColors.firstOrNull { it.toComposeColor() == initial?.color }?.id
            ?: availableColors.firstOrNull()?.id
            ?: 1
        mutableIntStateOf(foundId)
    }

    LaunchedEffect(availableColors) {
        if (availableColors.isNotEmpty() && availableColors.none { it.id == selectedColorId }) {
            selectedColorId = availableColors.first().id
        }
    }

    val selectedColorObj = availableColors.firstOrNull { it.id == selectedColorId }
        ?: availableColors.firstOrNull()
    val selectedComposeColor = selectedColorObj?.toComposeColor() ?: Color(0xFF3B82F6)
    val valid = name.isNotBlank() && !isLoading && availableColors.isNotEmpty()

    AppMainLayout(
        topBar = { CheckPointTopBar(title, onCancel) },
        contentGap = 24.dp,
        bottomBarHeight = 0.dp,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(heading, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextMain)
                Text(hint, fontSize = 13.sp, color = TextSub)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Outline),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Nama
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.take(30) },
                        label = { Text("Nama check point") },
                        placeholder = { Text("Contoh: Pengepakan") },
                        singleLine = true,
                        supportingText = { Text("${name.length}/30") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Pilihan warna
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Pilih warna", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextMain)
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            }
                        }

                        if (availableColors.isEmpty()) {
                            Text(
                                text = "Semua pilihan warna telah digunakan oleh check point lain.",
                                fontSize = 13.sp,
                                color = Danger
                            )
                        } else {
                            ColorPalette(
                                colors = availableColors,
                                selectedColorId = selectedColorId,
                                onSelect = { selectedColorId = it.id }
                            )

                            val colorName = selectedColorObj?.name
                            Text(
                                text = if (colorName != null) "Warna terpilih: $colorName" else "Pilih satu warna untuk check point ini.",
                                fontSize = 12.sp, color = TextSub
                            )
                        }
                    }

                    HorizontalDivider(color = Outline)

                    // Pratinjau
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Pratinjau", fontSize = 12.sp, color = TextSub)
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(selectedComposeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(selectedComposeColor))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                name.ifBlank { "Nama progress" },
                                fontSize = 12.sp, color = TextMain, fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton(if (isLoading) "Menyimpan..." else submitLabel, enabled = valid) {
                    onSubmit(name.trim(), selectedColorId)
                }
                SecondaryButton("Batal", onCancel)
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Layar 4: Dialog Konfirmasi Hapus
// ---------------------------------------------------------------------------
@Composable
private fun DeleteConfirmDialog(
    item: CheckPoint,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = CardBg) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DangerSoft),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Delete, null, Modifier.size(24.dp), tint = Danger) }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hapus check point?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextMain)
                    Text(
                        "Check point ini akan dihapus dari database dan tidak lagi tampil sebagai pilihan progres laundry.",
                        fontSize = 14.sp, color = TextSub
                    )
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface0)
                        .border(1.dp, Outline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) { CheckPointIdentity(item) }

                Text("Tindakan ini tidak dapat dibatalkan.", fontSize = 13.sp, color = Danger)

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Danger, contentColor = Color.White)
                    ) { Text("Ya, Hapus", fontWeight = FontWeight.SemiBold) }
                    SecondaryButton("Batal", onDismiss)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CheckPointScreenPreview() {
    LaundryFlowTheme {

    }
}