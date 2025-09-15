package com.rofiq.launcherly.common.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import kotlinx.coroutines.delay

@Composable
fun CardNotification( message: String, icon: ImageVector, durationMillis: Long = 3000){

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(durationMillis)
        visible = false
    }

    AnimatedVisibility(visible) {
        Card(
            colors = CardDefaults.cardColors(TVColors.Border),
            modifier = Modifier
                .width(320.dp)
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .background(TVColors.Border)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = "Error Icon",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(Modifier.padding(5.dp))

                Text(
                    text = message,
                    style = TVTypography.BodyRegular,
                    color = Color.White,
                    maxLines = 3,
                )
            }
        }
    }
}