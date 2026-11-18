package com.example.bpp_laundry.ui.preview


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.bpp_laundry.ui.components.*
import com.example.bpp_laundry.dashboard.*

import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.R

import com.example.bpp_laundry.ui.components.AppBackButton
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppOrderCard
import com.example.bpp_laundry.ui.components.AppPasswordField
import com.example.bpp_laundry.ui.components.AppTextBlock
import com.example.bpp_laundry.ui.components.AppTextButton
import com.example.bpp_laundry.ui.components.AppTextField
import com.example.bpp_laundry.ui.layout.AppFormLayout
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.bpp_laundry.ui.theme.AppText
import com.example.laundryflow.components.CheckPoint
import com.example.laundryflow.components.CheckPointCard
import com.example.laundryflow.components.CheckPointColor

@Preview(
    name = "App Layout",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun AppLayoutPreview() {
    AppLayout(
        topBar = {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "App Icon",
                modifier = Modifier.size(40.dp)
            )
        },

        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AppTextBlock(
                    header = "Catat pesanan dalam hitungan detik",
                    body = "Input pelanggan, jenis layanan, dan berat cucian dengan cepat. Kode pesanan dibuat otomatis.",
                )

                Spacer(modifier = Modifier.height(24.dp))

                var email by rememberSaveable {
                    mutableStateOf("")
                }

                var password by rememberSaveable {
                    mutableStateOf("")
                }

                AppFormLayout {
                    AppTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "nama@email.com",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        prefixIcon = Icons.Outlined.Search
                    )

                    AppPasswordField(
                        value = password,
                        onValueChange = { password = it },
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        AppTextButton(
                            "Lupa kata sandi?",
                            onClick = {}
                        )
                    }
                }



                AppButton("Lanjutkan", onClick = {})
                AppButton(
                    text = "Lanjut Dengan Google",
                    onClick = {},
                    backgroundColor = AppColors.Surface,
                    contentColor = AppColors.PrimaryApp,
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = AppColors.PrimaryApp
                    ),
                    leadingContent = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_icon),
                            contentDescription = "Google",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                AppBackButton(onClick={})
                var selectedTab by remember {
                    mutableStateOf(DashboardTab.Dashboard)
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppBottomNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        onAddClick = {
                            // Aksi saat tombol + ditekan
                        },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }

                val order = RecentOrder(
                    id = "LF-0251",
                    customer = "Rina Kartika",
                    detail = "2,0 kg • Cuci Kering",
                    status = OrderStatus.Diterima,
                    price = "Rp 12.000",
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppOrderCard(
                        order = order,
                        onDetailClick = { orderId ->
                            // Aksi saat tombol detail ditekan
                        },
                    )
                }

                AppOrderCard(
                    order = RecentOrder(
                        id = "#ORD-001",
                        customer = "Budi Santoso",
                        detail = "Cuci dan Setrika",
                        status = OrderStatus.Diproses,
                        price = "Rp25.000",
                    ),
                    onDetailClick = { orderId ->
                        // Aksi ketika detail diklik
                    },
                )

                AppOrderFilterChips(
                    options = listOf("Semua", "Diproses", "Siap Diambil", "Pengantaran"),
                    selected = "Semua",
                    onSelected = { selected ->
                        // Aksi ketika chip dipilih
                    },
                )

                var selectedService by remember { mutableStateOf<String?>(null) }

                AppDropdown(
                    label = "Jenis Layanan",
                    options = listOf("Cuci Kering", "Cuci + Setrika", "Express"),
                    selectedOption = selectedService,
                    onOptionSelected = { selectedService = it },
                    placeholder = "Pilih jenis layanan",

                )
                var isChecked by remember { mutableStateOf(false) }

                AppCard(
                    contentPadding = 16.dp,
                    cornerRadius = 16.dp,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Total Biaya",
                            style = AppText.TotalBiaya
                        )


                    }
                }
                AppDivider()
                AppCheckbox(
                    label = "Layanan antar",
                    description = "Pesanan diantar ke alamat pelanggan.",
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                )

                val checkPoints = remember {
                    mutableStateListOf(
                        CheckPoint(1, "Penerimaan", Color(0xFF3B82F6)),   // biru
                        CheckPoint(2, "Pencucian", Color(0xFF22C55E)),    // hijau
                        CheckPoint(3, "Pengeringan", Color(0xFFF97316)),  // oranye
                        CheckPoint(4, "Penyetrikaan", Color(0xFF8B5CF6))  // ungu
                    )
                }

                checkPoints.forEach { item ->

                    CheckPointCard(
                        item = item,

                        onEdit = { selected ->
                            // Aksi ketika tombol Edit diklik
                            println("Edit: ${selected.name}")
                        },

                        onDelete = { selected ->
                            // Aksi ketika tombol Hapus diklik
                            checkPoints.remove(selected)
                        }
                    )
                }


            }
        },

        bottomBar = {

        }


    )
}
