package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.R

@Composable
fun IpistoLogo(
    modifier: Modifier = Modifier,
    size: Int = 120,
    tintColor: Color = Color.Unspecified
) {
    val painter = painterResource(id = R.drawable.logo)
    val appliedModifier = modifier.size(size.dp)
    if (tintColor != Color.Unspecified) {
        val colorFilter = ColorFilter.tint(tintColor)
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = appliedModifier.clip(CircleShape),
            colorFilter = colorFilter
        )
    } else {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = appliedModifier.clip(CircleShape)
        )
    }
}

@Composable
fun IpistoLogoSmall(
    modifier: Modifier = Modifier,
    size: Int = 48,
    tintColor: Color = Color.Unspecified
) {
    val painter = painterResource(id = R.drawable.logo)
    val appliedModifier = modifier.size(size.dp)
    if (tintColor != Color.Unspecified) {
        val colorFilter = ColorFilter.tint(tintColor)
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = appliedModifier.clip(CircleShape),
            colorFilter = colorFilter
        )
    } else {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = appliedModifier.clip(CircleShape)
        )
    }
}