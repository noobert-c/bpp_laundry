
package com.example.bpp_laundry.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.bpp_laundry.ui.theme.AppColors

@Composable
fun WhatsAppSettingsScreen(
    onBack: () -> Unit = {},
    onSave: (
        businessNumber: String,
        destinationNumber: String,
        messageTemplate: String
    ) -> Unit = { _, _, _ -> }
) {
    var businessNumber by rememberSaveable {
        mutableStateOf("6281234567890")
    }

    var destinationNumber by rememberSaveable {
        mutableStateOf("")
    }

    var messageTemplate by rememberSaveable {
        mutableStateOf(
            "Halo {nama}, pesanan laundry Anda dengan nomor " +
                    "{kode_pesanan} sedang {status}. " +
                    "Total pembayaran: {total}."
        )
    }

    var showQrDialog by remember {
        mutableStateOf(false)
    }

    var showSavedMessage by remember {
        mutableStateOf(false)
    }

    AppMainLayout(
        horizontalPadding = 20.dp,
        verticalPadding = 16.dp,
        contentGap = 20.dp,
        bottomBarHeight = 0.dp,
        topBar = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Kembali",
                        tint = AppColors.TextMain
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Pengaturan WhatsApp",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain
                )
            }
        },
        content = {
            // NOMOR WHATSAPP BISNIS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Nomor WhatsApp Bisnis",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextMain
                    )

                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = "Terverifikasi",
                        tint = AppColors.PrimaryApp,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        AppColors.BorderApp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    AppColors.PrimaryApp.copy(alpha = 0.10f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "WA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.PrimaryApp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = businessNumber,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextMain
                            )

                            Text(
                                text = "Nomor WhatsApp bisnis",
                                fontSize = 12.sp,
                                color = AppColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Aktif",
                            tint = AppColors.PrimaryApp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showQrDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.PrimaryApp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCode2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Tampilkan QR WhatsApp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // NOMOR TUJUAN
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Nomor tujuan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain
                )

                OutlinedTextField(
                    value = destinationNumber,
                    onValueChange = {
                        destinationNumber = it.filter { char ->
                            char.isDigit() || char == '+'
                        }
                        showSavedMessage = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Nomor tujuan untuk simulasi kirim pesan",
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.PrimaryApp,
                        unfocusedBorderColor = AppColors.BorderApp,
                        focusedTextColor = AppColors.TextMain,
                        unfocusedTextColor = AppColors.TextMain,
                        cursorColor = AppColors.PrimaryApp,
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface
                    )
                )

                Text(
                    text = "Gunakan kode negara, contoh: 6281234567890.",
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
            }

            // TEMPLATE PESAN
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Template pesan WhatsApp",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain
                )

                OutlinedTextField(
                    value = messageTemplate,
                    onValueChange = {
                        messageTemplate = it
                        showSavedMessage = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 140.dp),
                    placeholder = {
                        Text(
                            text = "Tulis template pesan...",
                            fontSize = 13.sp
                        )
                    },
                    minLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.PrimaryApp,
                        unfocusedBorderColor = AppColors.BorderApp,
                        focusedTextColor = AppColors.TextMain,
                        unfocusedTextColor = AppColors.TextMain,
                        cursorColor = AppColors.PrimaryApp,
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface
                    )
                )

                Text(
                    text = "Variabel tersedia: {nama}, {kode_pesanan}, " +
                            "{status}, dan {total}.",
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
            }

            // TOMBOL SIMPAN BERADA DI DALAM CONTENT
            Button(
                onClick = {
                    onSave(
                        businessNumber,
                        destinationNumber.trim(),
                        messageTemplate
                    )
                    showSavedMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryApp,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Simpan Pengaturan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (showSavedMessage) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.PrimaryApp,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "Pengaturan berhasil dikirim.",
                        fontSize = 13.sp,
                        color = AppColors.PrimaryApp
                    )
                }
            }
        },
        bottomBar = {
            // Sengaja kosong.
        }
    )

    if (showQrDialog) {
        WhatsAppQrDialog(
            onDismiss = {
                showQrDialog = false
            }
        )
    }
}

@Composable
private fun WhatsAppQrDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = AppColors.Surface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QR WhatsApp",
                        modifier = Modifier.weight(1f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextMain
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Tutup",
                            tint = AppColors.TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            AppColors.BorderApp,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode2,
                            contentDescription = "Ilustrasi QR WhatsApp",
                            modifier = Modifier.size(220.dp),
                            tint = Color.Black
                        )

                        Text(
                            text = "QR WhatsApp",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                Text(
                    text = "Hubungkan WhatsApp",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain
                )

                Text(
                    text = "QR ini masih berupa ilustrasi dan belum dapat " +
                            "dipindai untuk menghubungkan WhatsApp.",
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                    lineHeight = 19.sp
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryApp,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Tutup",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
