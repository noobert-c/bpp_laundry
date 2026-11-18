package com.example.bpp_laundry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.dashboard.*
import com.example.bpp_laundry.ui.components.AppBottomNavigation
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.AppOrderFilterChips
import com.example.bpp_laundry.ui.components.AppStatusBadge
import com.example.bpp_laundry.ui.components.AppTextField
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.bpp_laundry.ui.theme.AppColors

/* -------------------------------------------------------------------------- */
/*  Design tokens                                                             */
/* -------------------------------------------------------------------------- */

private object OrdersColors {
    val Primary = Color(0xFF0F766E)
    val OnPrimary = Color.White
    val PrimaryTint = Color(0xFFCCFBF1)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
}

private val Inter: FontFamily = FontFamily.Default

private val CardShape = RoundedCornerShape(16.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private val ContentMaxWidth = 720.dp   // batas lebar di tablet

/* -------------------------------------------------------------------------- */
/*  Model UI                                                                  */
/* -------------------------------------------------------------------------- */

data class OrderItem(
    val id: String,
    val customer: String,
    val detail: String,
    val status: OrderStatus,
    val price: String,
)

enum class OrderFilter(val label: String) {
    Semua("Semua"),
    Diproses("Diproses"),
    SiapDiambil("Siap Diambil"),
    Pengantaran("Pengantaran"),
}

/**
 * [orders] diharapkan sudah difilter/diurutkan oleh ViewModel berdasarkan
 * [query], [selectedFilter], dan urutan sortir.
 */
data class OrderListUiState(
    val query: String = "",
    val selectedFilter: OrderFilter = OrderFilter.Semua,
    val userInitials: String = "MA",
    val orders: List<OrderItem> = sampleOrders,
)

private val sampleOrders = listOf(
    OrderItem("LF-0251", "Rina Kartika", "2,0 kg  •  Cuci Kering", OrderStatus.Diterima, "Rp 12.000"),
    OrderItem("LF-0250", "Andi Pratama", "4,0 kg  •  Cuci + Setrika", OrderStatus.Dicuci, "Rp 32.000"),
    OrderItem("LF-0249", "Dewi Lestari", "6,5 kg  •  Cuci + Setrika", OrderStatus.Disetrika, "Rp 52.000"),
)

/* -------------------------------------------------------------------------- */
/*  Screen                                                                    */
/* -------------------------------------------------------------------------- */

@Composable
fun OrderListScreen(
    state: OrderListUiState = OrderListUiState(),
    onQueryChange: (String) -> Unit = {},
    onFilterSelected: (OrderFilter) -> Unit = {},
    onSortClick: () -> Unit = {},
    onShuttleClick: () -> Unit = {},
    onOrderDetailClick: (orderId: String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
    onAddOrderClick: () -> Unit = {},
) {
    AppMainLayout(
        // Padding horizontal 20dp sesuai desain; semua item di content tidak perlu padding sendiri.
        horizontalPadding = 20.dp,
        verticalPadding = 12.dp,
        contentGap = 12.dp,
        topBar = {
            Header(
                initials = state.userInitials,
                onNotificationClick = onNotificationClick,
                onAvatarClick = onAvatarClick,
            )
        },
        content = {
            // Batasi lebar di tablet dan pusatkan.
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = ContentMaxWidth)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = "Cari pesanan...",
                    prefixIcon = Icons.Outlined.Search,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Search,
                )

                AppOrderFilterChips(
                    options = OrderFilter.entries.map { it.label },
                    selected = state.selectedFilter.label,
                    onSelected = { selectedLabel ->
                        OrderFilter.entries
                            .firstOrNull { it.label == selectedLabel }
                            ?.let(onFilterSelected)
                    },
                    contentPadding = PaddingValues(0.dp),
                )

                AppButton(
                    text = "Lihat Antar Jemput",
                    onClick = onShuttleClick,
                    backgroundColor = Color.White,
                    contentColor = OrdersColors.Primary,
                    textColor = OrdersColors.Primary,
                    border = BorderStroke(1.dp, OrdersColors.Primary),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = OrdersColors.Primary,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                )

                if (state.orders.isEmpty()) {
                    Text(
                        text = "Tidak ada pesanan yang cocok.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = OrdersColors.Muted,
                        ),
                    )
                } else {
                    // Tidak boleh LazyColumn: content sudah verticalScroll.
                    state.orders.forEach { order ->
                        key(order.id) {
                            OrderCard(
                                order = order,
                                onDetailClick = { onOrderDetailClick(order.id) },
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedTab = DashboardTab.Pesanan, // sesuaikan dengan nama entry di DashboardTab
                onTabSelected = onTabSelected,
                onAddClick = onAddOrderClick,
            )
        },
    )
}

/* -------------------------------------------------------------------------- */
/*  Header (dipakai sebagai isi topBar, tinggi 54dp diatur AppMainLayout)     */
/* -------------------------------------------------------------------------- */

@Composable
private fun Header(
    initials: String,
    onNotificationClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Daftar Pesanan",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = OrdersColors.Text,
            ),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

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
}

/* -------------------------------------------------------------------------- */
/*  Pencarian + sortir                                                        */
/* -------------------------------------------------------------------------- */

@Composable
private fun SearchAndSortRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = OrdersColors.Text,
            ),
            cursorBrush = SolidColor(OrdersColors.Primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(FieldShape)
                        .background(OrdersColors.Surface)
                        .border(
                            width = if (focused) 1.5.dp else 1.dp,
                            color = if (focused) OrdersColors.Primary else OrdersColors.Border,
                            shape = FieldShape,
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = OrdersColors.Muted,
                        modifier = Modifier.size(20.dp),
                    )
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Cari pesanan atau pelanggan",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = OrdersColors.Muted,
                                ),
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(FieldShape)
                .background(OrdersColors.PrimaryTint)
                .border(1.dp, OrdersColors.Primary, FieldShape)
                .clickable(role = Role.Button, onClick = onSortClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort,
                contentDescription = "Urutkan pesanan",
                tint = OrdersColors.Primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Filter status (LazyRow horizontal aman di dalam verticalScroll)           */
/* -------------------------------------------------------------------------- */

@Composable
private fun FilterChips(
    selected: OrderFilter,
    onSelected: (OrderFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(OrderFilter.entries.toList()) { filter ->
            val isSelected = filter == selected
            val shape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(shape)
                    .background(if (isSelected) OrdersColors.Primary else OrdersColors.Surface)
                    .then(
                        if (isSelected) Modifier else Modifier.border(1.dp, OrdersColors.Border, shape),
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
                        color = if (isSelected) OrdersColors.OnPrimary else OrdersColors.Muted,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ShuttleButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(FieldShape)
            .background(OrdersColors.Surface)
            .border(1.dp, OrdersColors.Primary, FieldShape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.LocalShipping,
            contentDescription = null,
            tint = OrdersColors.Primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Lihat Antar Jemput",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = OrdersColors.Primary,
            ),
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  Kartu pesanan                                                             */
/* -------------------------------------------------------------------------- */

@Composable
private fun OrderCard(
    order: OrderItem,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(OrdersColors.Surface)
            .border(1.dp, OrdersColors.Border, CardShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = order.id,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = OrdersColors.Muted,
                ),
            )
            Text(
                text = order.customer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = OrdersColors.Text,
                ),
            )
            Text(
                text = order.detail,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = OrdersColors.Muted,
                ),
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppStatusBadge(
                text = order.status.label,
                backgroundColor = order.status.background,
                textColor = order.status.content,
            )
            Text(
                text = order.price,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = OrdersColors.Primary,
                ),
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(OrdersColors.Background)
                .clickable(role = Role.Button, onClick = onDetailClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Lihat detail pesanan ${order.id}",
                tint = OrdersColors.Muted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Preview                                                                   */
/* -------------------------------------------------------------------------- */

@Preview(name = "Phone 393x852", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun OrderListPhonePreview() = OrderListScreen()

@Preview(name = "Small phone 320x568", widthDp = 320, heightDp = 568, showBackground = true)
@Composable
private fun OrderListSmallPreview() = OrderListScreen()

@Preview(name = "Tablet 800x1280", widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
private fun OrderListTabletPreview() = OrderListScreen()

@Preview(name = "Kosong", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun OrderListEmptyPreview() = OrderListScreen(state = OrderListUiState(orders = emptyList()))