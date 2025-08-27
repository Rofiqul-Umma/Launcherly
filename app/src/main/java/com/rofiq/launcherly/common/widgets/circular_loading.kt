package com.rofiq.launcherly.common.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rofiq.launcherly.common.color.TVColors

@Composable
@Preview
fun LCircularLoading(size: Int = 24, strokeWidth: Int = 2) {
    CircularProgressIndicator(
        color = TVColors.OnSurfaceSecondary,
        modifier = Modifier.size(size.dp),
        strokeWidth = strokeWidth.dp
    )
}