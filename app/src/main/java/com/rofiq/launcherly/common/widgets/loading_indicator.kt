package com.rofiq.launcherly.common.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rofiq.launcherly.common.color.TVColors

@ExperimentalMaterial3ExpressiveApi
@Composable
fun LoadingIndicator(
    color: Color = TVColors.OnSurfaceSecondary,
    size: Int = 35
) {
    CircularWavyProgressIndicator(
        modifier = Modifier
            .size(size.dp),
        color = color
    )
}