package com.example.weatherapp.ui.home.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.ui.theme.RamadanGold

@Composable
fun HeaderSection(
    cityName: String,
    isDetailMode: Boolean = false,
    navController: NavController? = null,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isDetailMode) {
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = textColor)
                }
                Spacer(Modifier.width(8.dp))
            } else {
                // Location icon ONLY on Home
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = RamadanGold.copy(alpha = 0.2f)
                ) {
                    Icon(Icons.Default.LocationOn, null, Modifier.padding(8.dp), RamadanGold)
                }
                Spacer(Modifier.width(12.dp))
            }

            Column {
                Text(cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
            }
        }


    }
}
