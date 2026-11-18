
package com.example.bpp_laundry

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.bpp_laundry.dashboard.DashboardTab
import com.example.bpp_laundry.data.repository.CustomerRepository
import com.example.bpp_laundry.ui.components.AppBottomNavigation
import com.example.bpp_laundry.ui.components.AppButton
import com.example.bpp_laundry.ui.components.CustomerCard
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.bpp_laundry.ui.theme.AppColors
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
    currentUserId: Int = 3,
    onQueryChange: (String) -> Unit = {},
    onSortSelected: (CustomerSort) -> Unit = {},
    onCustomerClick: (customerId: String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
    onAddOrderClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CustomerRepository() }

    var searchQuery by remember { mutableStateOf(state.query) }
    var currentSort by remember { mutableStateOf(state.sort) }
    var customersList by remember { mutableStateOf(state.customers) }

    var showContactDialog by remember { mutableStateOf(false) }
    var isSavingCustomer by remember { mutableStateOf(false) }

    val displayedCustomers = remember(customersList, searchQuery, currentSort) {
        val filtered = if (searchQuery.isBlank()) {
            customersList
        } else {
            customersList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
            }
        }
        when (currentSort) {
            CustomerSort.NameAsc -> filtered.sortedBy { it.name.lowercase() }
            CustomerSort.MostOrders -> filtered.sortedByDescending { it.orderCount }
            CustomerSort.Newest -> filtered
        }
    }

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
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onQueryChange(it)
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showContactDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = AppColors.PrimaryApp
                        ),
                        border = BorderStroke(1.dp, AppColors.PrimaryApp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = AppColors.PrimaryApp,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Pelanggan",
                            color = AppColors.PrimaryApp
                        )
                    }

                    if (showContactDialog) {
                        InputContactDialog(
                            isLoading = isSavingCustomer,
                            onDismiss = {
                                if (!isSavingCustomer) {
                                    showContactDialog = false
                                }
                            },
                            onConfirm = { name, phone ->
                                scope.launch {
                                    isSavingCustomer = true
                                    val result = repository.createCustomer(
                                        name = name,
                                        phone = phone,
                                        userId = currentUserId
                                    )
                                    isSavingCustomer = false
                                    result.onSuccess { created ->
                                        showContactDialog = false
                                        Toast.makeText(
                                            context,
                                            "Pelanggan berhasil disimpan ke database",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Tambahkan pelanggan baru ke daftar tampilan (static list)
                                        val newCustomer = Customer(
                                            id = created.id.toString(),
                                            name = created.name,
                                            phone = created.phone ?: phone,
                                            orderCount = 0
                                        )
                                        customersList = listOf(newCustomer) + customersList
                                    }.onFailure { error ->
                                        Toast.makeText(
                                            context,
                                            error.message ?: "Gagal menyimpan customer",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        ListControls(
                            count = displayedCustomers.size,
                            sort = currentSort,
                            onSortSelected = {
                                currentSort = it
                                onSortSelected(it)
                            }
                        )
                    }
                }

                if (displayedCustomers.isEmpty()) {
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
                    displayedCustomers.forEach { customer ->
                        CustomerCard(
                            name = customer.name,
                            phone = customer.phone,
                            orderCount = customer.orderCount,
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
            .height(56.dp),
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

// ---------- Konstanta lokal (ubah warna di sini sesuai tema Anda) ----------
private object ContactDialogColors {
    val Surface = Color.White
    val Primary = AppColors.PrimaryApp      // TODO: samakan dengan warna utama aplikasi
    val PrimaryTint = Color(0x370E6C65)
    val Text = Color(0xFF111827)
    val Muted = Color(0xFF6B7280)
}

private val ContactDialogShape = RoundedCornerShape(20.dp)
private val ContactDialogMaxWidth = 400.dp

@Composable
fun InputContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String) -> Unit,
    isLoading: Boolean = false,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0.4f) }

        var name by rememberSaveable { mutableStateOf("") }
        var phone by rememberSaveable { mutableStateOf("") }

        val phoneFocus = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        val canSubmit = name.isNotBlank() && phone.length in 9..13

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = ContactDialogMaxWidth)
                .fillMaxWidth()
                .clip(ContactDialogShape)
                .background(ContactDialogColors.Surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ContactDialogColors.PrimaryTint),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = ContactDialogColors.Primary,
                    modifier = Modifier.size(28.dp),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Data Pelanggan",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = ContactDialogColors.Text,
                )
                Text(
                    text = "Masukkan nama dan nomor telepon untuk melanjutkan.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = ContactDialogColors.Muted,
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ContactTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nama",
                    placeholder = "Nama lengkap",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { phoneFocus.requestFocus() },
                    ),
                )
                ContactTextField(
                    value = phone,
                    onValueChange = { input -> phone = input.filter { it.isDigit() }.take(13) },
                    label = "Nomor Telepon",
                    placeholder = "08xxxxxxxxxx",
                    modifier = Modifier.focusRequester(phoneFocus),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ContactDialogButton(
                    text = "Batal",
                    primary = false,
                    onClick = onDismiss,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                )
                ContactDialogButton(
                    text = if (isLoading) "Menyimpan..." else "Simpan",
                    primary = true,
                    enabled = canSubmit && !isLoading,
                    onClick = { onConfirm(name.trim(), phone) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ContactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = ContactDialogColors.Text,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = ContactDialogColors.Muted,
                )
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = ContactDialogColors.Text,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ContactDialogColors.Primary,
                unfocusedBorderColor = ContactDialogColors.Muted.copy(alpha = 0.4f),
                cursorColor = ContactDialogColors.Primary,
            ),
        )
    }
}

@Composable
private fun ContactDialogButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp)
            .alpha(if (enabled) 1f else 0.5f),
        shape = RoundedCornerShape(12.dp),
        elevation = null,
        border = if (primary) null
        else BorderStroke(1.dp, ContactDialogColors.Muted.copy(alpha = 0.4f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) ContactDialogColors.Primary else ContactDialogColors.Surface,
            contentColor = if (primary) Color.White else ContactDialogColors.Text,
            disabledContainerColor = if (primary) ContactDialogColors.Primary else ContactDialogColors.Surface,
            disabledContentColor = if (primary) Color.White else ContactDialogColors.Text,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}