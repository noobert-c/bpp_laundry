
package com.example.bpp_laundry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.dashboard.DashboardTab
import com.example.bpp_laundry.ui.components.AppBottomNavigation
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppCard
import com.example.bpp_laundry.ui.components.AppCheckbox
import com.example.bpp_laundry.ui.components.AppDivider
import com.example.bpp_laundry.ui.components.AppDropdown
import com.example.bpp_laundry.ui.components.AppTextField
import com.example.bpp_laundry.ui.layout.AppFormLayout
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.bpp_laundry.ui.theme.AppText

@Composable
fun NewAppOrderScreen() {
    var selectedTab by remember {
        mutableStateOf(DashboardTab.Dashboard)
    }

    var nama by rememberSaveable {
        mutableStateOf("")
    }

    var cucian by rememberSaveable {
        mutableStateOf("")
    }

    var tarif by rememberSaveable {
        mutableStateOf("8.000")
    }

    var selectedService by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var isBayarSekarang by rememberSaveable {
        mutableStateOf(false)
    }

    var isAntar by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedMetode by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    AppMainLayout(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "BPP Laundry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain,
                )

                IconButton(
                    onClick = {
                        // Aksi pengaturan atau notifikasi
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Pengaturan",
                        tint = AppColors.TextMain,
                    )
                }
            }
        },
        content = {
            AppFormLayout {
                AppTextField(
                    label = "Nama atau Nomor",
                    value = nama,
                    onValueChange = { nama = it },
                    placeholder = "Nama atau nomor pelanggan",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    prefixIcon = Icons.Outlined.Search,
                )

                AppDropdown(
                    label = "Jenis Layanan",
                    options = listOf(
                        "Cuci Kering",
                        "Cuci + Setrika",
                        "Express",
                    ),
                    selectedOption = selectedService,
                    onOptionSelected = { selectedService = it },
                    placeholder = "Pilih jenis layanan",
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    AppTextField(
                        label = "Berat Cucian (Kg)",
                        value = cucian,
                        onValueChange = { cucian = it },
                        placeholder = "Masukkan berat",
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f),
                    )

                    AppTextField(
                        label = "Tarif Per Kg",
                        value = tarif,
                        onValueChange = { tarif = it },
                        placeholder = "Masukkan tarif",
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f),
                    )
                }

                AppCheckbox(
                    label = "Bayar Sekarang",
                    checked = isBayarSekarang,
                    onCheckedChange = { isBayarSekarang = it },
                )

                AppCheckbox(
                    label = "Antar",
                    checked = isAntar,
                    onCheckedChange = { isAntar = it },
                )

                AppDropdown(
                    label = "Metode Pembayaran",
                    options = listOf("Tunai", "QR"),
                    selectedOption = selectedMetode,
                    onOptionSelected = { selectedMetode = it },
                    placeholder = "Pilih metode pembayaran",
                )

                AppCard(
                    contentPadding = 16.dp,
                    cornerRadius = 16.dp,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Total Biaya",
                            style = AppText.TotalBiaya,
                        )

                        AppDivider()

                        Text(
                            text = "500k",
                            style = AppText.BiayaUi,
                        )
                    }
                }


            }
        },
        bottomBar = {
            AppButton(
                text = "Simpan Pesanan",
                onClick = {
                    // Aksi menyimpan pesanan
                },
            )
        },
        bottomBarOffset = 34.dp,
        bottomBarPadding = 0.dp
    )
}
