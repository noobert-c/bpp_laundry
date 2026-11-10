package com.example.bpp_laundry



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

/* -------------------------------------------------------------------------- */
/*  Design tokens (dari metadata Figma node 1:4193)                           */
/* -------------------------------------------------------------------------- */

private object SettingsColors {
    val Primary = Color(0xFF0F7B6C)
    val OnPrimary = Color.White
    val PrimaryTint = Color(0xFFD1F5EC)
    val Background = Color(0xFFF6F8FA)
    val Surface = Color.White
    val BorderSoft = Color(0xFFE2E8F0)   // tombol kembali
    val Border = Color(0xFFDDE7E6)       // kartu, divider
    val Muted = Color(0xFF6B7280)
    val Text = Color(0xFF111827)
    val Teal = Color(0xFF0F766E)         // judul "Paket dan Harga" (sesuai metadata)
    val Danger = Color(0xFFDC2626)
    val DangerTint = Color(0xFFFEF2F2)
}

/**
 * Font Inter. Tambahkan file font ke res/font lalu ganti, contoh:
 * FontFamily(Font(R.font.inter_regular), Font(R.font.inter_medium, FontWeight.Medium), ...)
 */
private val Inter: FontFamily = FontFamily.Default

private val CardShape = RoundedCornerShape(16.dp)
private val ContentMaxWidth = 560.dp   // batas lebar konten di tablet
private val DialogMaxWidth = 342.dp    // lebar dialog di desain

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

data class SettingsUiState(
    val initials: String = "MA",
    val adminName: String = "Admin Laundry",
    val ownerLine: String = "Maya • Pemilik usaha",
    val businessName: String = "LaundryFlow",
)

private enum class MenuAction { WhatsApp, PackagesAndPrices, About, Checkpoint, Logout }

private data class MenuItemData(
    val action: MenuAction,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val titleColor: Color = SettingsColors.Text,
    val iconTint: Color = SettingsColors.Primary,
    val iconBackground: Color = SettingsColors.PrimaryTint,
)

private val menuItems = listOf(
    MenuItemData(
        MenuAction.WhatsApp, "Pengaturan WhatsApp", "Atur pesan otomatis untuk pelanggan",
        Icons.Outlined.ChatBubbleOutline,
    ),
    MenuItemData(
        MenuAction.PackagesAndPrices, "Paket dan Harga", "Kelola paket pesanan anda",
        Icons.Outlined.Inventory2, titleColor = SettingsColors.Teal,
    ),
    MenuItemData(
        MenuAction.About, "Tentang Aplikasi", "Informasi aplikasi dan versi terbaru",
        Icons.Outlined.Info,
    ),
    MenuItemData(
        MenuAction.Checkpoint, "Check Point", "Kelola titik progress laundry",
        Icons.Outlined.Place,
    ),
    MenuItemData(
        MenuAction.Logout, "Keluar", "Keluar dari akun pada perangkat ini",
        Icons.AutoMirrored.Outlined.Logout,
        titleColor = SettingsColors.Danger,
        iconTint = SettingsColors.Danger,
        iconBackground = SettingsColors.DangerTint,
    ),
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@Composable
fun SettingsScreen(
    state: SettingsUiState = SettingsUiState(),
    onBackClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {},          // -> Pengaturan WhatsApp (1:2479, asumsi)
    onPackagesAndPricesClick: () -> Unit = {}, // -> Paket dan Harga (1:2325, asumsi)
    onAboutClick: () -> Unit = {},             // -> Tentang aplikasi (1:955, asumsi)
    onCheckpointClick: () -> Unit = {},        // -> Check Point (1:3832, asumsi)
    onLogoutConfirmed: () -> Unit = {},        // hapus sesi lalu ke 05 Login (1:762)
    initialShowLogoutDialog: Boolean = false,
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(initialShowLogoutDialog) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxSize(),
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
                    text = "Pengaturan",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = SettingsColors.Text,
                    ),
                )
            }

            // ----- Konten (scroll) -----
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp), // content_gap
            ) {
                ProfileCard(state)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "PENGATURAN USAHA",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            letterSpacing = 0.6.sp,
                            color = SettingsColors.Muted,
                        ),
                    )
                    MenuList(
                        items = menuItems,
                        onItemClick = { action ->
                            when (action) {
                                MenuAction.WhatsApp -> onWhatsAppClick()
                                MenuAction.PackagesAndPrices -> onPackagesAndPricesClick()
                                MenuAction.About -> onAboutClick()
                                MenuAction.Checkpoint -> onCheckpointClick()
                                MenuAction.Logout -> showLogoutDialog = true
                            }
                        },
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogoutConfirmed()
            },
        )
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
            .background(SettingsColors.Surface)
            .border(1.dp, SettingsColors.BorderSoft, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Kembali",
            tint = SettingsColors.Text,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ProfileCard(state: SettingsUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(SettingsColors.Surface)
            .border(1.dp, SettingsColors.Border, CardShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SettingsColors.PrimaryTint),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = state.initials,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = SettingsColors.Primary,
                ),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = state.adminName,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = SettingsColors.Text,
                ),
            )
            Text(
                text = state.ownerLine,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = SettingsColors.Muted,
                ),
            )
            // Gaya teks nama usaha tidak ada di metadata (asumsi).
            Text(
                text = state.businessName,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = SettingsColors.Primary,
                ),
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Daftar menu: kartu putih, border 1dp #DDE7E6, radius 16                   */
/*  Item: padding 16x18, gap 12, ikon 40 (radius 12) / 20, chevron 16,        */
/*  divider 1dp #DDE7E6 di antara item.                                       */
/* -------------------------------------------------------------------------- */

@Composable
private fun MenuList(
    items: List<MenuItemData>,
    onItemClick: (MenuAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(SettingsColors.Surface)
            .border(1.dp, SettingsColors.Border, CardShape),
    ) {
        items.forEachIndexed { index, item ->
            MenuRow(item = item, onClick = { onItemClick(item.action) })
            if (index != items.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SettingsColors.Border),
                )
            }
        }
    }
}

@Composable
private fun MenuRow(item: MenuItemData, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = item.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = item.titleColor,
                ),
            )
            Text(
                text = item.subtitle,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = SettingsColors.Muted,
                ),
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = SettingsColors.Muted,
            modifier = Modifier.size(16.dp),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Dialog konfirmasi keluar                                                  */
/*  Kartu putih radius 16, padding 24, gap 20, lebar maks 342.                */
/*  Latar redup rgba(17,24,39,0.4) -> dim window 0.4 (warna dim dari sistem). */
/* -------------------------------------------------------------------------- */

@Composable
private fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        // Samakan tingkat redup latar dengan desain (40%).
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0.4f) }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp) // jarak ke tepi layar
                .widthIn(max = DialogMaxWidth)
                .fillMaxWidth()
                .clip(CardShape)
                .background(SettingsColors.Surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Lambang keluar: lingkaran 64dp, ikon 28dp
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SettingsColors.PrimaryTint),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = SettingsColors.Primary,
                    modifier = Modifier.size(28.dp),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Keluar dari Akun?",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = SettingsColors.Text,
                    ),
                )
                Text(
                    text = "Anda yakin ingin keluar dari akun LaundryFlow? Anda perlu masuk kembali untuk mengelola laundry.",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 20.sp, // 1,4 x 14sp
                        color = SettingsColors.Muted,
                    ),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DialogButton(
                    text = "Batal",
                    primary = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                DialogButton(
                    text = "Ya, Keluar",
                    primary = true,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Tombol dialog: tinggi 48, radius 12, Medium 16. Sekunder: border 1,5dp. */
@Composable
private fun DialogButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(if (primary) SettingsColors.Primary else SettingsColors.Surface)
            .then(
                if (primary) Modifier else Modifier.border(1.5.dp, SettingsColors.Primary, shape),
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = if (primary) SettingsColors.OnPrimary else SettingsColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 390x844", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun SettingsPhonePreview() = SettingsScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun SettingsSmallPreview() = SettingsScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun SettingsTabletPreview() = SettingsScreen()

@Preview(name = "Dialog keluar", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun SettingsLogoutDialogPreview() = SettingsScreen(initialShowLogoutDialog = true)