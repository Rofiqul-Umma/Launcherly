package com.rofiq.launcherly.common.text_style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rofiq.launcherly.R

object TVTypography {
    val HeaderLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
    )
    val BodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp,
    )

    val BodyRegular = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp,
    )

    val BodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp,
    )
}