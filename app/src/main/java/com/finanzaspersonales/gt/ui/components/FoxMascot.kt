package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.finanzaspersonales.gt.ui.theme.IpistoPalette

/** Original, lightweight Compose illustration used as iPisto's friendly visual cue. */
@Composable
fun FoxMascot(modifier: Modifier = Modifier, size: Dp = 76.dp) {
    Canvas(modifier.size(size).semantics { contentDescription = "Zorrito lavanda de iPisto" }) {
        val w = this.size.width
        val h = this.size.height
        val outline = Color(0xFF202942)
        val ears = Path().apply {
            moveTo(w * .18f, h * .47f); lineTo(w * .16f, h * .08f); lineTo(w * .43f, h * .27f)
            lineTo(w * .57f, h * .27f); lineTo(w * .84f, h * .08f); lineTo(w * .82f, h * .47f)
            close()
        }
        drawPath(ears, outline)
        val head = Path().apply {
            moveTo(w * .17f, h * .43f)
            quadraticBezierTo(w * .20f, h * .20f, w * .50f, h * .23f)
            quadraticBezierTo(w * .80f, h * .20f, w * .83f, h * .43f)
            quadraticBezierTo(w * .88f, h * .76f, w * .50f, h * .91f)
            quadraticBezierTo(w * .12f, h * .76f, w * .17f, h * .43f)
            close()
        }
        drawPath(head, IpistoPalette.Lavender)
        drawPath(head, outline, style = Stroke(width = w * .035f))
        val leftEar = Path().apply { moveTo(w * .24f, h * .35f); lineTo(w * .21f, h * .16f); lineTo(w * .39f, h * .30f); close() }
        val rightEar = Path().apply { moveTo(w * .76f, h * .35f); lineTo(w * .79f, h * .16f); lineTo(w * .61f, h * .30f); close() }
        drawPath(leftEar, IpistoPalette.Peach)
        drawPath(rightEar, IpistoPalette.Peach)
        drawCircle(outline, radius = w * .035f, center = androidx.compose.ui.geometry.Offset(w * .37f, h * .52f))
        drawCircle(outline, radius = w * .035f, center = androidx.compose.ui.geometry.Offset(w * .63f, h * .52f))
        val muzzle = Path().apply {
            moveTo(w * .29f, h * .65f); quadraticBezierTo(w * .50f, h * .55f, w * .71f, h * .65f)
            quadraticBezierTo(w * .62f, h * .82f, w * .50f, h * .83f)
            quadraticBezierTo(w * .38f, h * .82f, w * .29f, h * .65f); close()
        }
        drawPath(muzzle, IpistoPalette.Peach)
        drawCircle(outline, radius = w * .033f, center = androidx.compose.ui.geometry.Offset(w * .50f, h * .65f))
    }
}
