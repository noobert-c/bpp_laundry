package com.example.laundryflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bpp_laundry.ui.screens.WhatsAppSettingsScreen
import kotlinx.coroutines.launch
import kotlin.random.Random

// ---------------------------------------------------------------------------
// Warna & tema (ganti dengan design token Figma yang sebenarnya)
// ---------------------------------------------------------------------------
private val Primary = Color(0xFF2563EB)
private val Surface0 = Color(0xFFF6F8FB)
private val CardBg = Color.White
private val Outline = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextSub = Color(0xFF64748B)
private val Danger = Color(0xFFDC2626)
private val DangerSoft = Color(0xFFFEE2E2)
private val WaGreen = Color(0xFF16A34A)

@Composable
fun AkunTheme(content: @Composable () -> Unit) {
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

private sealed interface AkunPage {
    data object Home : AkunPage
    data object WhatsApp : AkunPage
    data object About : AkunPage
}

// ---------------------------------------------------------------------------
// Activity & root
// ---------------------------------------------------------------------------
class AkunActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AkunTheme {
                AkunFeature(
                    onOpenCheckPoint = { /* buka CheckPointFeature() */ },
                    onOpenPaket = { /* buka PaketHargaFeature() */ },
                    onLogout = { /* hapus sesi lalu ke layar login */ },
                )
            }
        }
    }
}

@Composable
fun AkunFeature(
    onOpenCheckPoint: () -> Unit,
    onOpenPaket: () -> Unit,
    onLogout: () -> Unit,
) {
    var page by remember { mutableStateOf<AkunPage>(AkunPage.Home) }
    var showLogout by remember { mutableStateOf(false) }

    BackHandler(enabled = page != AkunPage.Home) { page = AkunPage.Home }

    when (page) {
        AkunPage.Home -> AkunHomeScreen(
            onWhatsApp = { page = AkunPage.WhatsApp },
            onPaket = onOpenPaket,
            onAbout = { page = AkunPage.About },
            onCheckPoint = onOpenCheckPoint,
            onLogout = { showLogout = true },
        )

        AkunPage.WhatsApp -> WhatsAppSettingsScreen(onBack = { page = AkunPage.Home })
        AkunPage.About -> AboutScreen(onBack = { page = AkunPage.Home })
    }

    if (showLogout) {
        LogoutDialog(
            onConfirm = { showLogout = false; onLogout() },
            onDismiss = { showLogout = false },
        )
    }
}

// ---------------------------------------------------------------------------
// Komponen bersama
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageTitleBar(title: String, onBack: (() -> Unit)?) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Surface0,
            titleContentColor = TextMain,
        )
    )
}

@Composable
private fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold)
    }
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
private fun WhiteCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, Outline),
        modifier = modifier.fillMaxWidth(),
        content = content
    )
}

// ---------------------------------------------------------------------------
// Layar 1: Akun
// ---------------------------------------------------------------------------
@Composable
private fun AkunHomeScreen(
    onWhatsApp: () -> Unit,
    onPaket: () -> Unit,
    onAbout: () -> Unit,
    onCheckPoint: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(containerColor = Surface0, topBar = { PageTitleBar("Akun", null) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profil admin
            WhiteCard {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(56.dp).clip(CircleShape).background(Primary),
                        contentAlignment = Alignment.Center
                    ) { Text("AD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Admin LaundryFlow", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextMain)
                        Text("admin@laundryflow.id", fontSize = 14.sp, color = TextSub)
                        Text("Pemilik Usaha", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Preferensi usaha
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Preferensi Usaha", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextSub)
                WhiteCard {
                    SettingsMenuItem(Icons.Default.Chat, "WhatsApp", "Atur nomor dan template pesan", onClick = onWhatsApp)
                    HorizontalDivider(color = Outline)
                    SettingsMenuItem(Icons.Default.Inventory2, "Paket & Harga", "Kelola layanan dan tarif", onClick = onPaket)
                    HorizontalDivider(color = Outline)
                    SettingsMenuItem(Icons.Default.Info, "Tentang", "Informasi aplikasi LaundryFlow", onClick = onAbout)
                    HorizontalDivider(color = Outline)
                    SettingsMenuItem(Icons.Default.LocationOn, "Check Point", "Kelola titik progress laundry", onClick = onCheckPoint)
                    HorizontalDivider(color = Outline)
                    SettingsMenuItem(
                        Icons.AutoMirrored.Filled.Logout, "Keluar", "Akhiri sesi di perangkat ini",
                        destructive = true, onClick = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    description: String,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    val accent = if (destructive) Danger else Primary
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, Modifier.size(20.dp), tint = accent) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = if (destructive) Danger else TextMain)
            Text(description, fontSize = 13.sp, color = TextSub)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, Modifier.size(16.dp), tint = TextSub)
    }
}

// ---------------------------------------------------------------------------
// Dialog Logout
// ---------------------------------------------------------------------------
@Composable
private fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = CardBg) {
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(DangerSoft),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.AutoMirrored.Filled.Logout, null, Modifier.size(28.dp), tint = Danger) }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Keluar dari akun?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextMain)
                    Text(
                        "Anda perlu masuk kembali untuk mengelola pesanan, paket, dan pengaturan usaha.",
                        fontSize = 14.sp, color = TextSub, textAlign = TextAlign.Center
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton("Batal", Modifier.weight(1f), onDismiss)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Danger, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) { Text("Ya, Keluar", fontWeight = FontWeight.SemiBold, maxLines = 1) }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Layar 2: Pengaturan WhatsApp (+ popup QR)
// ---------------------------------------------------------------------------
@Composable
private fun WhatsAppSettingsScreen1(onBack: () -> Unit) {
    var targetNumber by remember { mutableStateOf("") }
    var template by remember {
        mutableStateOf(
            "Halo {nama}, cucian Anda saat ini berada di tahap {progress}. " +
                "Terima kasih telah memakai layanan kami."
        )
    }
    var showQr by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Surface0,
        topBar = { PageTitleBar("Pengaturan WhatsApp", onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nomor WhatsApp bisnis + status terverifikasi
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nomor WhatsApp Bisnis", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextMain)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.CheckCircle, "Terverifikasi", Modifier.size(16.dp), tint = WaGreen)
            }
            PrimaryButton("Tampilkan Kode QR", icon = Icons.Default.QrCode) { showQr = true }

            // Nomor tujuan simulasi
            OutlinedTextField(
                value = targetNumber,
                onValueChange = { targetNumber = it.filter { c -> c.isDigit() || c == '+' }.take(16) },
                label = { Text("Nomor tujuan") },
                placeholder = { Text("Nomor tujuan untuk simulasi kirim pesan") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Editor template
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Template Pesan", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextMain)
                OutlinedTextField(
                    value = template,
                    onValueChange = { template = it.take(300) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(118.dp),
                )
                Text(
                    "Gunakan {nama} dan {progress} sebagai pengganti otomatis. ${template.length}/300",
                    fontSize = 12.sp, color = TextSub
                )
            }

            PrimaryButton("Simpan Pengaturan") {
                scope.launch { snackbar.showSnackbar("Pengaturan WhatsApp disimpan") }
            }
        }
    }

    if (showQr) QrDialog(onClose = { showQr = false })
}

@Composable
private fun QrDialog(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(shape = RoundedCornerShape(20.dp), color = CardBg) {
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, "Tutup", Modifier.size(18.dp), tint = TextSub)
                        }
                    }
                    StaticQrIllustration(Modifier.size(280.dp))
                    Spacer(Modifier.height(12.dp))
                }
            }
            Text(
                "Ilustrasi kode QR statis (bukan kode QR sungguhan)",
                color = Color.White, fontSize = 13.sp, textAlign = TextAlign.Center
            )
        }
    }
}

/** Gambar mirip QR (statis) untuk ilustrasi. Ganti dengan QR asli dari library/backend jika dibutuhkan. */
@Composable
private fun StaticQrIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val n = 25
        val cell = size.width / n
        val rnd = Random(7)

        fun inFinder(r: Int, c: Int) =
            (r < 8 && c < 8) || (r < 8 && c >= n - 8) || (r >= n - 8 && c < 8)

        for (r in 0 until n) for (c in 0 until n) {
            if (!inFinder(r, c) && rnd.nextBoolean()) {
                drawRect(TextMain, Offset(c * cell, r * cell), Size(cell, cell))
            }
        }

        fun finder(row: Int, col: Int) {
            val x = col * cell
            val y = row * cell
            drawRect(TextMain, Offset(x, y), Size(7 * cell, 7 * cell))
            drawRect(Color.White, Offset(x + cell, y + cell), Size(5 * cell, 5 * cell))
            drawRect(TextMain, Offset(x + 2 * cell, y + 2 * cell), Size(3 * cell, 3 * cell))
        }
        finder(0, 0)
        finder(0, n - 7)
        finder(n - 7, 0)
    }
}

// ---------------------------------------------------------------------------
// Layar 3: Tentang Aplikasi
// ---------------------------------------------------------------------------
@Composable
private fun AboutScreen(onBack: () -> Unit) {
    Scaffold(containerColor = Surface0, topBar = { PageTitleBar("Tentang", onBack) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Identitas aplikasi
            Column(
                Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(72.dp).clip(RoundedCornerShape(20.dp)).background(Primary),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Checkroom, null, Modifier.size(40.dp), tint = Color.White) }
                Text("LaundryFlow", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextMain)
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Primary.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) { Text("Versi 1.0.0", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium) }
            }

            Text(
                "LaundryFlow membantu pemilik laundry mencatat pesanan, memantau progres cucian, " +
                    "dan menginformasikan pelanggan secara cepat lewat WhatsApp.",
                fontSize = 14.sp, color = TextSub, textAlign = TextAlign.Center
            )

            WhiteCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Fitur Utama", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextMain)
                    FeatureRow(Icons.Default.Checklist, "Manajemen Pesanan", "Catat dan kelola cucian pelanggan")
                    FeatureRow(Icons.Default.Group, "Data Pelanggan", "Simpan kontak dan riwayat pelanggan")
                    FeatureRow(Icons.Default.BarChart, "Laporan Usaha", "Pantau pendapatan dan performa")
                    FeatureRow(Icons.Default.Chat, "Notifikasi WhatsApp", "Kirim kabar progres ke pelanggan")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Dikembangkan oleh Tim LaundryFlow", fontSize = 13.sp, color = TextSub)
                Text("© 2026 LaundryFlow. Hak cipta dilindungi.", fontSize = 13.sp, color = TextSub)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, Modifier.size(20.dp), tint = Primary) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextMain)
            Text(description, fontSize = 12.sp, color = TextSub)
        }
    }
}
