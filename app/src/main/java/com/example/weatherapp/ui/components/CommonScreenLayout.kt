package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CommonScreenLayout(
    title: String,
    description: String,
    isEmpty: Boolean,
    emptyContent: @Composable () -> Unit,
    content: @Composable BoxScope.() -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    selectionBar: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.55f),
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            if (isEmpty) {
                emptyContent()
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            floatingActionButton()
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            selectionBar()
        }
    }
}
