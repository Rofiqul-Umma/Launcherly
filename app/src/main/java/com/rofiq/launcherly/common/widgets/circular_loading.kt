package com.rofiq.launcherly.common.widgets

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rofiq.launcherly.common.color.TVColors

@Composable
@Preview
fun LCircularLoading() {
    CircularProgressIndicator(
        color = TVColors.OnSurfaceSecondary
    )
}