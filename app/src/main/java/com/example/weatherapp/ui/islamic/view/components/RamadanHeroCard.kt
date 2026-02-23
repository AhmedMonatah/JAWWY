package com.example.weatherapp.ui.islamic.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold
import com.example.weatherapp.utils.islamic.IslamicDateInfo

@Composable
fun RamadanHeroCard(info: IslamicDateInfo) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(RamadanDeepNavy, RamadanDarkBlue)
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "${info.day} ${stringResource(info.monthResId)} ${info.year} ${stringResource(R.string.hijri_suffix)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = RamadanGold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (info.isRamadan)
                    stringResource(R.string.ramadan_kareem)
                else
                    stringResource(R.string.ramadan_countdown),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.ramadan_hero_subtitle),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                ),
                maxLines = 2
            )

            if (!info.isRamadan) {

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.Bottom) {

                    Text(
                        text = info.daysToRamadan.toString(),
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = RamadanGold,
                            fontWeight = FontWeight.Black
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.days_remaining),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}