package com.example.weatherapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState

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
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
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

        val snackbarHostState = LocalSnackbarHostState.current
        val hasSnackbar = snackbarHostState.currentSnackbarData != null
        val fabPadding by animateDpAsState(
            targetValue = if (hasSnackbar) 80.dp else 13.dp,
            label = "fab_padding"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = fabPadding, end = 13.dp),
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
