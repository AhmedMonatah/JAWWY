package com.example.weatherapp.ui.favorites.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.LocalIsDark

@Composable
fun TypeSelector(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isDark = LocalIsDark.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ).then(
                if (selected) {
                    if (isDark) {
                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                    } else {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    }
                } else Modifier
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
