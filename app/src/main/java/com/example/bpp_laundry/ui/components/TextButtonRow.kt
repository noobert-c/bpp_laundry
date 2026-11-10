
package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.theme.AppText

@Composable
fun AppTextButtonRow(
    text: String,
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gap: Dp = 4.dp,
    horizontalArrangement : Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment : Alignment.Vertical = Alignment.CenterVertically

) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AppText.Body
        )

        Spacer(modifier = Modifier.width(gap))

        AppTextButton(
            text = buttonText,
            onClick = onClick
        )
    }
}
