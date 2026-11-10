package com.example.bpp_laundry

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import com.example.bpp_laundry.ui.components.AppTopSpacer
import java.util.Locale
import kotlin.math.roundToLong

/* -------------------------------------------------------------------------- */
/*  Tema & parameter layout                                                   */
/*  Catatan: teks/warna bertanda "ASUMSI" tidak ada di metadata Figma.        */
/* -------------------------------------------------------------------------- */

private object AddOrderColors {
    val Primary = Color(0xFF0F766E)
    val OnPrimary = Color.White
    val Background = Color(0xFFF8FAFC)   // ASUMSI
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
}

/** Parameter dari spesifikasi layout. */
private object AddOrderLayout {
    val HorizontalPadding = 24.dp
    val VerticalPadding = 16.dp
    val ContentGap = 16.dp
    val TopBarHeight = 54.dp
    val TopBarElevation = 8.dp
    const val TopBarShadowAlpha = 0.10f
    val BottomBarHeight = 104.dp
    val BottomBarOffset = 0.dp
    val BottomBarPadding = 0.dp
    val ContentMaxWidth = 560.dp
    val DialogMaxWidth = 342.dp
}

/** Ganti dengan font Inter dari res/font bila sudah ada. */
private val Inter: FontFamily = FontFamily.Default

private val FieldShape = RoundedCornerShape(12.dp)
private val CardShape = RoundedCornerShape(16.dp)

private fun inter(
    size: TextUnit,
    weight: FontWeight = FontWeight.Normal,
    color: Color = AddOrderColors.Text,
    lineHeight: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = Inter,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    color = color,
)

private val InputTextStyle = inter(14.sp)

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

data class ServiceOption(val name: String, val ratePerKg: Long)

data class OrderOption(
    val id: String,
    val label: String,
    val description: String? = null,
    val checked: Boolean = false,
)

data class AddOrderUiState(
    val customerQuery: String = "",
    val selectedCustomer: String? = null,
    val customerSuggestions: List<String> = emptyList(),
    val services: List<ServiceOption> = sampleServices,
    val selectedService: ServiceOption? = null,
    val weight: String = "",          // kg, boleh koma atau titik
    val ratePerKg: String = "",       // hanya digit
    val receivedDate: String = "",    // sudah diformat, mis. "8 Nov 2026"
    val estimateDate: String = "",
    val options: List<OrderOption> = sampleOptions,
    val paymentMethods: List<String> = samplePaymentMethods,
    val selectedPayment: String? = null,
) {
    val weightValue: Double? get() = weight.replace(',', '.').toDoubleOrNull()
    val rateValue: Long? get() = ratePerKg.toLongOrNull()
    val total: Long get() = ((weightValue ?: 0.0) * (rateValue ?: 0L)).roundToLong()

    /** Ada data terisi -> tampilkan dialog konfirmasi saat keluar. */
    val isDirty: Boolean
        get() = customerQuery.isNotBlank() || selectedService != null || weight.isNotBlank() ||
                ratePerKg.isNotBlank() || selectedPayment != null

    /** Syarat tombol simpan aktif (ASUMSI: tanggal opsional). */
    val canSave: Boolean
        get() = selectedCustomer != null && selectedService != null &&
                (weightValue ?: 0.0) > 0.0 && (rateValue ?: 0L) > 0L && selectedPayment != null
}

private val sampleServices = listOf(
    ServiceOption("Cuci Kering", 6_000),
    ServiceOption("Cuci + Setrika", 8_000),
    ServiceOption("Express", 12_000),
)

// ASUMSI: label & deskripsi checkbox.
private val sampleOptions = listOf(
    OrderOption("notify_wa", "Bayar Sekarang", checked = false),
    OrderOption("delivery", "Layanan antar", "Tambahan biaya berdasarkan jarak"),
)

// ASUMSI: metode pembayaran.
private val samplePaymentMethods = listOf("Tunai", "QRIS", "Transfer Bank")

private fun formatRupiah(value: Long): String =
    "Rp " + String.format(Locale.forLanguageTag("id-ID"), "%,d", value)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@Composable
fun AddOrderScreen(
    state: AddOrderUiState = AddOrderUiState(),
    onCustomerQueryChange: (String) -> Unit = {},
    onCustomerSelected: (String) -> Unit = {},
    onServiceSelected: (ServiceOption) -> Unit = {},   // ViewModel: isi ratePerKg dari layanan
    onWeightChange: (String) -> Unit = {},
    onRateChange: (String) -> Unit = {},
    onReceivedDateClick: () -> Unit = {},
    onEstimateDateClick: () -> Unit = {},
    onOptionToggle: (id: String, checked: Boolean) -> Unit = { _, _ -> },
    onPaymentSelected: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onExit: () -> Unit = {},
) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    fun requestExit() {
        if (state.isDirty) showExitDialog = true else onExit()
    }
    BackHandler(onBack = ::requestExit)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AddOrderColors.Background),
        contentAlignment = Alignment.TopCenter,
    ) {
        // Kolom utama dibatasi lebarnya untuk tablet.
        Column(
            modifier = Modifier
                .widthIn(max = AddOrderLayout.ContentMaxWidth)
                .fillMaxSize(),
        ) {
            TopBar(
                title = "Tambah Pesanan", // ASUMSI
                onBack = ::requestExit,
            )

            val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(
                        start = AddOrderLayout.HorizontalPadding,
                        end = AddOrderLayout.HorizontalPadding,
                        top = AddOrderLayout.VerticalPadding,
                        bottom = AddOrderLayout.VerticalPadding +
                                AddOrderLayout.BottomBarHeight +
                                navBarBottom,
                    ),
                verticalArrangement = Arrangement.spacedBy(AddOrderLayout.ContentGap),
            ){
                OrderForm(
                    state = state,
                    onCustomerQueryChange = onCustomerQueryChange,
                    onCustomerSelected = onCustomerSelected,
                    onServiceSelected = onServiceSelected,
                    onWeightChange = onWeightChange,
                    onRateChange = onRateChange,
                    onReceivedDateClick = onReceivedDateClick,
                    onEstimateDateClick = onEstimateDateClick,
                    onOptionToggle = onOptionToggle,
                    onPaymentSelected = onPaymentSelected,
                )
            }
        }

        // Bottom bar mengambang di atas konten.
        BottomBar(
            enabled = state.canSave,
            onSaveClick = onSaveClick,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                showExitDialog = false
                onExit()
            },
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Top bar & bottom bar                                                      */
/* -------------------------------------------------------------------------- */

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f) // agar shadow tampil di atas konten yang di-scroll
            .shadow(
                elevation = AddOrderLayout.TopBarElevation,
                ambientColor = AddOrderColors.Text.copy(alpha = AddOrderLayout.TopBarShadowAlpha),
                spotColor = AddOrderColors.Text.copy(alpha = AddOrderLayout.TopBarShadowAlpha),
            )
            .background(AddOrderColors.Surface)
            .statusBarsPadding(),
    ) {
        AppTopSpacer()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AddOrderLayout.TopBarHeight)
                .padding(horizontal = AddOrderLayout.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AddOrderColors.Surface)
                    .border(1.dp, AddOrderColors.Border, CircleShape)
                    .clickable(role = Role.Button, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Kembali",
                    tint = AddOrderColors.Text,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(text = title, style = inter(24.sp, FontWeight.SemiBold))
        }
    }
}

@Composable
private fun BottomBar(
    enabled: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = AddOrderLayout.ContentMaxWidth)
            .navigationBarsPadding()
            .offset(y = -AddOrderLayout.BottomBarOffset)
            .padding(AddOrderLayout.BottomBarPadding)
            .padding(horizontal = AddOrderLayout.HorizontalPadding)
            .height(AddOrderLayout.BottomBarHeight),
        contentAlignment = Alignment.Center,
    ) {
        SaveButton(
            text = "Simpan Pesanan", // ASUMSI
            enabled = enabled,
            onClick = onSaveClick,
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Isi formulir                                                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun OrderForm(
    state: AddOrderUiState,
    onCustomerQueryChange: (String) -> Unit,
    onCustomerSelected: (String) -> Unit,
    onServiceSelected: (ServiceOption) -> Unit,
    onWeightChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onReceivedDateClick: () -> Unit,
    onEstimateDateClick: () -> Unit,
    onOptionToggle: (id: String, checked: Boolean) -> Unit,
    onPaymentSelected: (String) -> Unit,
) {
    CustomerSection(
        query = state.customerQuery,
        suggestions = state.customerSuggestions,
        selectedCustomer = state.selectedCustomer,
        onQueryChange = onCustomerQueryChange,
        onSelected = onCustomerSelected,
    )

    SelectField(
        label = "Jenis Layanan",
        placeholder = "Pilih jenis layanan",
        selectedText = state.selectedService?.name,
        options = state.services,
        optionLabel = { "${it.name} • ${formatRupiah(it.ratePerKg)}/kg" },
        onSelected = onServiceSelected,
    )

    TwoColumnRow {
        TextInputField(
            label = "Berat Cucian (kg)",
            value = state.weight,
            onValueChange = { onWeightChange(it.filter { c -> c.isDigit() || c == ',' || c == '.' }.take(6)) },
            placeholder = "0",
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.weight(1f),
        )
        TextInputField(
            label = "Tarif per Kg",
            value = state.ratePerKg,
            onValueChange = { onRateChange(it.filter(Char::isDigit).take(9)) },
            placeholder = "0",
            prefix = "Rp",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f),
        )
    }



    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        state.options.forEach { option ->
            OptionRow(option = option, onCheckedChange = { onOptionToggle(option.id, it) })
        }
    }

    SelectField(
        label = "Metode Pembayaran",
        placeholder = "Pilih metode pembayaran",
        selectedText = state.selectedPayment,
        options = state.paymentMethods,
        optionLabel = { it },
        onSelected = onPaymentSelected,
    )

    TotalSummary(weight = state.weightValue, rate = state.rateValue, total = state.total)
}

@Composable
private fun TwoColumnRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AddOrderLayout.ContentGap),
        content = content,
    )
}

/* -------------------------------------------------------------------------- */
/*  Pelanggan: kolom cari + saran                                             */
/* -------------------------------------------------------------------------- */

@Composable
private fun CustomerSection(
    query: String,
    suggestions: List<String>,
    selectedCustomer: String?,
    onQueryChange: (String) -> Unit,
    onSelected: (String) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val shown = suggestions.take(5)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel("Nama Pelanggan")

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = InputTextStyle,
            cursorBrush = SolidColor(AddOrderColors.Primary),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                FieldContainer(focused = focused) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = AddOrderColors.Muted,
                        modifier = Modifier.size(20.dp),
                    )
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) Placeholder("Cari atau pilih pelanggan") // ASUMSI
                        inner()
                    }
                    if (selectedCustomer != null) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Pelanggan dipilih",
                            tint = AddOrderColors.Primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            },
        )

        if (shown.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FieldShape)
                    .background(AddOrderColors.Surface)
                    .border(1.dp, AddOrderColors.Border, FieldShape),
            ) {
                shown.forEachIndexed { index, name ->
                    Text(
                        text = name,
                        style = InputTextStyle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button) { onSelected(name) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                    if (index != shown.lastIndex) Divider()
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AddOrderColors.Border),
    )
}

/* -------------------------------------------------------------------------- */
/*  Komponen field (label 19dp + gap 6 + input 48)                            */
/* -------------------------------------------------------------------------- */

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = inter(13.sp, FontWeight.Medium, lineHeight = 19.sp))
}

@Composable
private fun Placeholder(text: String) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = inter(14.sp, color = AddOrderColors.Muted),
    )
}

/** Kotak input: tinggi 48, radius 12, border 1dp, padding horizontal 16. */
@Composable
private fun FieldContainer(
    focused: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(FieldShape)
            .background(AddOrderColors.Surface)
            .border(
                width = if (focused) 1.5.dp else 1.dp,
                color = if (focused) AddOrderColors.Primary else AddOrderColors.Border,
                shape = FieldShape,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

/** Teks terpilih, atau placeholder bila kosong. */
@Composable
private fun RowScope.ValueOrPlaceholder(value: String?, placeholder: String) {
    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        if (value.isNullOrEmpty()) {
            Placeholder(placeholder)
        } else {
            Text(
                text = value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = InputTextStyle,
            )
        }
    }
}

@Composable
private fun TextInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    prefix: String? = null,
) {
    var focused by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = InputTextStyle,
            cursorBrush = SolidColor(AddOrderColors.Primary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                FieldContainer(focused = focused) {
                    if (prefix != null) {
                        Text(text = prefix, style = inter(14.sp, FontWeight.Medium, AddOrderColors.Muted))
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) Placeholder(placeholder)
                        inner()
                    }
                }
            },
        )
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        Box(Modifier.clickable(role = Role.Button, onClick = onClick)) {
            FieldContainer {
                ValueOrPlaceholder(value, "Pilih tanggal") // ASUMSI
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = AddOrderColors.Muted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/** Dropdown: kolom tampilan + DropdownMenu selebar kolom. */
@Composable
private fun <T> SelectField(
    label: String,
    placeholder: String,
    selectedText: String?,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var fieldWidthPx by remember { mutableIntStateOf(0) }
    val fieldWidth: Dp = with(LocalDensity.current) { fieldWidthPx.toDp() }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        Box {
            Box(
                Modifier
                    .onSizeChanged { fieldWidthPx = it.width }
                    .clickable(role = Role.DropdownList) { expanded = true },
            ) {
                FieldContainer(focused = expanded) {
                    ValueOrPlaceholder(selectedText, placeholder)
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = AddOrderColors.Muted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(fieldWidth)
                    .background(AddOrderColors.Surface),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = optionLabel(option), style = InputTextStyle) },
                        onClick = {
                            expanded = false
                            onSelected(option)
                        },
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Checkbox opsi                                                             */
/* -------------------------------------------------------------------------- */

@Composable
private fun OptionRow(option: OrderOption, onCheckedChange: (Boolean) -> Unit) {
    val boxShape = RoundedCornerShape(6.dp)
    val boxColor = if (option.checked) AddOrderColors.Primary else AddOrderColors.Surface
    val borderColor = if (option.checked) AddOrderColors.Primary else AddOrderColors.Border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = option.checked,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(boxShape)
                .background(boxColor)
                .border(1.dp, borderColor, boxShape),
            contentAlignment = Alignment.Center,
        ) {
            if (option.checked) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = AddOrderColors.OnPrimary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = option.label, style = inter(14.sp, FontWeight.Medium))
            option.description?.let {
                Text(text = it, style = inter(12.sp, color = AddOrderColors.Muted))
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Ringkasan total                                                           */
/* -------------------------------------------------------------------------- */

@Composable
private fun TotalSummary(weight: Double?, rate: Long?, total: Long) {
    val breakdown = if (weight != null && weight > 0.0 && rate != null && rate > 0L) {
        val weightText = if (weight % 1.0 == 0.0) weight.toLong().toString()
        else weight.toString().replace('.', ',')
        "$weightText kg × ${formatRupiah(rate)}"
    } else {
        "Isi berat dan tarif untuk melihat total"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(AddOrderColors.Surface)
            .border(1.dp, AddOrderColors.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = "Total Biaya", style = inter(12.sp, FontWeight.Medium, AddOrderColors.Muted)) // ASUMSI
        Text(text = formatRupiah(total), style = inter(22.sp, FontWeight.Bold, AddOrderColors.Primary))
        Text(text = breakdown, style = inter(12.sp, color = AddOrderColors.Muted))
    }
}

/** Tombol simpan: tinggi 48, radius 12. Nonaktif (alpha 50%) sampai data lengkap. */
@Composable
private fun SaveButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(FieldShape)
            .background(AddOrderColors.Primary)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = inter(16.sp, FontWeight.SemiBold, AddOrderColors.OnPrimary))
    }
}

/* -------------------------------------------------------------------------- */
/*  Dialog konfirmasi keluar (ASUMSI: teks)                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0.4f) }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = AddOrderLayout.DialogMaxWidth)
                .fillMaxWidth()
                .clip(CardShape)
                .background(AddOrderColors.Surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Keluar dari halaman ini?",
                    textAlign = TextAlign.Center,
                    style = inter(18.sp, FontWeight.SemiBold),
                )
                Text(
                    text = "Data pesanan yang sudah diisi belum disimpan dan akan hilang.",
                    textAlign = TextAlign.Center,
                    style = inter(14.sp, color = AddOrderColors.Muted, lineHeight = 20.sp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DialogButton("Batal", primary = false, onClick = onDismiss, modifier = Modifier.weight(1f))
                DialogButton("Ya, Keluar", primary = true, onClick = onConfirm, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DialogButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(FieldShape)
            .background(if (primary) AddOrderColors.Primary else AddOrderColors.Surface)
            .then(if (primary) Modifier else Modifier.border(1.5.dp, AddOrderColors.Primary, FieldShape))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = inter(
                16.sp,
                FontWeight.Medium,
                if (primary) AddOrderColors.OnPrimary else AddOrderColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun AddOrderPreviewHost(initial: AddOrderUiState = AddOrderUiState()) {
    var state by remember { mutableStateOf(initial) }
    AddOrderScreen(
        state = state,
        onCustomerQueryChange = { q ->
            state = state.copy(
                customerQuery = q,
                selectedCustomer = null,
                customerSuggestions = if (q.isBlank()) emptyList()
                else listOf("Rina Kartika", "Andi Pratama", "Dewi Lestari")
                    .filter { it.contains(q, ignoreCase = true) },
            )
        },
        onCustomerSelected = {
            state = state.copy(customerQuery = it, selectedCustomer = it, customerSuggestions = emptyList())
        },
        onServiceSelected = { state = state.copy(selectedService = it, ratePerKg = it.ratePerKg.toString()) },
        onWeightChange = { state = state.copy(weight = it) },
        onRateChange = { state = state.copy(ratePerKg = it) },
        onOptionToggle = { id, checked ->
            state = state.copy(options = state.options.map { if (it.id == id) it.copy(checked = checked) else it })
        },
        onPaymentSelected = { state = state.copy(selectedPayment = it) },
    )
}

@Preview(name = "Phone 393x852", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun AddOrderPhonePreview() = AddOrderPreviewHost()

@Preview(name = "Terisi", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun AddOrderFilledPreview() = AddOrderPreviewHost(
    AddOrderUiState(
        customerQuery = "Rina Kartika",
        selectedCustomer = "Rina Kartika",
        selectedService = sampleServices[1],
        weight = "6,5",
        ratePerKg = "8000",
        receivedDate = "8 Nov 2026",
        estimateDate = "10 Nov 2026",
        selectedPayment = "QRIS",
    ),
)

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun AddOrderSmallPreview() = AddOrderPreviewHost()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun AddOrderTabletPreview() = AddOrderPreviewHost()