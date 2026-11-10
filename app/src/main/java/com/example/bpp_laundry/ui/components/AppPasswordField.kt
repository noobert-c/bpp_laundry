
package com.example.bpp_laundry.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Kata Sandi",
    placeholder: String = "Masukkan kata sandi",
    imeAction: ImeAction = ImeAction.Done,
) {
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    AppTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        prefixIcon = Icons.Outlined.Lock,
        suffixIcon = if (passwordVisible) {
            Icons.Outlined.VisibilityOff
        } else {
            Icons.Outlined.Visibility
        },
        onSuffixClick = {
            passwordVisible = !passwordVisible
        },
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
    )
}
