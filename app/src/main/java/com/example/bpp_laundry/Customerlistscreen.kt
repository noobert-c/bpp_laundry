
package com.example.bpp_laundry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.dashboard.DashboardTab
import com.example.bpp_laundry.ui.components.AppBottomNavigation
import com.example.bpp_laundry.ui.layout.AppMainLayout

private object CustomerColors {
    val Primary = Color(0xFF0F766E)
    val PrimaryTint = Color(0xFFCCFBF1)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
}

private val CardShape = RoundedCornerShape(16.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private val ContentMaxWidth = 720.dp

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val orderCount: Int,
)

enum class CustomerSort(val label: String) {
    NameAsc("Nama A–Z"),
    MostOrders("Terbanyak"),
    Newest("Terbaru"),
}

private val sampleCustomers = listOf(
    Customer("c1", "Andi Pratama", "0812-3456-7801", 12),
    Customer("c2", "Budi Santoso", "0813-2200-4512", 8),
    Customer("c3", "Dewi Lestari", "0857-9011-3320", 15),
    Customer("c4", "Rina Kartika", "0821-4455-6678", 6),
    Customer("c5", "Siti Rahmawati", "0878-1234-9087", 3),
)

data class CustomerListUiState(
    val query: String = "",
    val sort: CustomerSort = CustomerSort.NameAsc,
    val userInitials: String = "MA",
    val customers: List<Customer> = sampleCustomers,
    val customerCount: Int = customers.size,
)

@Composable
fun CustomerListScreen(
    state: CustomerListUiState = CustomerListUiState(),
    onQueryChange: (String) -> Unit = {},
    onSortSelected: (CustomerSort) -> Unit = {},
    onCustomerClick: (customerId: String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
    onAddOrderClick: () -> Unit = {},
) {
    AppMainLayout(
        modifier = Modifier.background(CustomerColors.Background),
        topBar = {
            Header(
                initials = state.userInitials,
                onNotificationClick = onNotificationClick,
                onAvatarClick = onAvatarClick,
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = ContentMaxWidth),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SearchField(
                    query = state.query,
                    onQueryChange = onQueryChange,
                )

                ListControls(
                    count = state.customerCount,
                    sort = state.sort,
                    onSortSelected = onSortSelected,
                )

                if (state.customers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Tidak ada pelanggan yang cocok.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = CustomerColors.Muted,
                            ),
                        )
                    }
                } else {
                    state.customers.forEach { customer ->
                        CustomerCard(
                            customer = customer,
                            onClick = {
                                onCustomerClick(customer.id)
                            },
                        )
                    }
                }
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedTab = DashboardTab.Pelanggan,
                onTabSelected = onTabSelected,
                onAddClick = onAddOrderClick,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        bottomBarHeight = 104.dp,
        bottomBarPadding = 0.dp,
    )
}

@Composable
private fun Header(
    initials: String,
    onNotificationClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = ContentMaxWidth)
            .height(56.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Pelanggan",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = CustomerColors.Text,
            ),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CustomerColors.Surface)
                    .border(1.dp, CustomerColors.Border, CircleShape)
                    .clickable(
                        role = Role.Button,
                        onClick = onNotificationClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = CustomerColors.Text,
                    modifier = Modifier.size(20.dp),
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CustomerColors.PrimaryTint)
                    .clickable(
                        role = Role.Button,
                        onClick = onAvatarClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CustomerColors.Primary,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = CustomerColors.Text,
        ),
        cursorBrush = SolidColor(CustomerColors.Primary),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            },
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                focused = it.isFocused
            },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(FieldShape)
                    .background(CustomerColors.Surface)
                    .border(
                        width = if (focused) 1.5.dp else 1.dp,
                        color = if (focused) {
                            CustomerColors.Primary
                        } else {
                            CustomerColors.Border
                        },
                        shape = FieldShape,
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = CustomerColors.Muted,
                    modifier = Modifier.size(20.dp),
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Cari pelanggan",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = CustomerColors.Muted,
                            ),
                        )
                    }

                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun ListControls(
    count: Int,
    sort: CustomerSort,
    onSortSelected: (CustomerSort) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "$count pelanggan",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = CustomerColors.Muted,
            ),
        )

        SortDropdown(
            selected = sort,
            onSelected = onSortSelected,
        )
    }
}

@Composable
private fun SortDropdown(
    selected: CustomerSort,
    onSelected: (CustomerSort) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .widthIn(min = 140.dp)
                .height(40.dp)
                .clip(FieldShape)
                .background(CustomerColors.Surface)
                .border(1.dp, CustomerColors.Border, FieldShape)
                .clickable(
                    role = Role.DropdownList,
                    onClick = { expanded = true },
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.SwapVert,
                contentDescription = null,
                tint = CustomerColors.Muted,
                modifier = Modifier.size(16.dp),
            )

            Text(
                text = selected.label,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = CustomerColors.Text,
                ),
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = CustomerColors.Muted,
                modifier = Modifier.size(16.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CustomerColors.Surface),
        ) {
            CustomerSort.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = if (option == selected) {
                                CustomerColors.Primary
                            } else {
                                CustomerColors.Text
                            },
                            fontWeight = if (option == selected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
private fun CustomerCard(
    customer: Customer,
    onClick: () -> Unit,
) {
    val orderTotal = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = CustomerColors.Primary,
            ),
        ) {
            append(customer.orderCount.toString())
        }

        append(" pesanan")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(CustomerColors.Surface)
            .border(1.dp, CustomerColors.Border, CardShape)
            .clickable(
                onClickLabel = "Lihat ${customer.name}",
                role = Role.Button,
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = customer.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CustomerColors.Text,
                ),
            )

            Text(
                text = customer.phone,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = CustomerColors.Muted,
                ),
            )

            Text(
                text = orderTotal,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CustomerColors.Muted,
                ),
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CustomerColors.Background),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = CustomerColors.Muted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview(
    name = "Customer List",
    widthDp = 393,
    heightDp = 852,
    showBackground = true,
)
@Composable
private fun CustomerListPreview() {
    CustomerListScreen()
}

@Preview(
    name = "Customer List Empty",
    widthDp = 393,
    heightDp = 852,
    showBackground = true,
)
@Composable
private fun CustomerListEmptyPreview() {
    CustomerListScreen(
        state = CustomerListUiState(
            customers = emptyList(),
            customerCount = 0,
        ),
    )
}
