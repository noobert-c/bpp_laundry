package com.example.bpp_laundry.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private object AppTextFieldColors {
    val Background = Color.White
    val Border = Color(0xFFDDE7E6)
    val Primary = Color(0xFF0F7B6C)
    val Label = Color(0xFF6B7280)
    val Text = Color(0xFF111827)
    val Placeholder = Color(0xFF9CA3AF)
}

@Composable
fun AppTextField(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    prefixIcon: ImageVector? = null,
    suffixIcon: ImageVector? = null,
    onSuffixClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation =
        VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    icon_size : Dp = 20.dp
) {
    var focused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(12.dp)
    val borderColor =
        if (focused) AppTextFieldColors.Primary
        else AppTextFieldColors.Border

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            if (label != null) 6.dp else 0.dp
        ),
    ) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AppTextFieldColors.Label,
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = AppTextFieldColors.Text,
            ),
            cursorBrush = SolidColor(AppTextFieldColors.Primary),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = keyboardActions,
            interactionSource = remember {
                MutableInteractionSource()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .onFocusChanged {
                    focused = it.isFocused
                },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(shape)
                        .background(AppTextFieldColors.Background)
                        .border(
                            width = if (focused) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = shape,
                        )
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (prefixIcon != null) {
                        Icon(
                            imageVector = prefixIcon,
                            contentDescription = null,
                            tint = if (focused) {
                                AppTextFieldColors.Primary
                            } else {
                                AppTextFieldColors.Label
                            },
                            modifier = Modifier.size(icon_size),
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 14.sp,
                                color = AppTextFieldColors.Placeholder,
                            )
                        }

                        innerTextField()
                    }

                    if (suffixIcon != null) {
                        Icon(
                            imageVector = suffixIcon,
                            contentDescription = null,
                            tint = AppTextFieldColors.Label,
                            modifier = Modifier
                                .size(icon_size)
                                .then(
                                    if (onSuffixClick != null) {
                                        Modifier.clickable(
                                            onClick = onSuffixClick,
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                        )
                    }
                }
            },
        )
    }
}
