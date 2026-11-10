
package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.bpp_laundry.ui.theme.AppText
import androidx.compose.material3.ripple

@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AppText.TextButton,
    textColor: Color = AppColors.PrimaryAuth,
    enabled: Boolean = true
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Text(
        text = text,
        modifier = modifier
            .padding(2.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick
            ),
        style = textStyle,
        color = if (enabled) textColor else AppColors.TextSecondary,
    )
}