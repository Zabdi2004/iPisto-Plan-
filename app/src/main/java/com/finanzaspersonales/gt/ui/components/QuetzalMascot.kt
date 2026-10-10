package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Original flat quetzal illustration, drawn locally so it works offline. */
@Composable
fun QuetzalMascot(modifier: Modifier = Modifier, size: Dp = 76.dp) {
    val emerald = Color(0xFF63C8A2)
    val teal = Color(0xFF55C7D4)
    val coral = Color(0xFFEF817E)
    val navy = Color(0xFF202942)
    Canvas(modifier.size(size).semantics { contentDescription = "Quetzal verde de iPisto" }) {
        val w = this.size.width; val h = this.size.height
        // Long tail plumes behind the body.
        val tail = Path().apply {
            moveTo(w * .54f, h * .67f); quadraticBezierTo(w * .82f, h * .73f, w * .94f, h * .98f)
            quadraticBezierTo(w * .68f, h * .91f, w * .48f, h * .74f); close()
        }
        drawPath(tail, teal); drawPath(tail, navy, style = Stroke(w * .025f))
        val feather = Path().apply {
            moveTo(w * .47f, h * .67f); quadraticBezierTo(w * .70f, h * .79f, w * .76f, h * .99f)
            quadraticBezierTo(w * .54f, h * .89f, w * .39f, h * .75f); close()
        }
        drawPath(feather, emerald); drawPath(feather, navy, style = Stroke(w * .02f))
        // Head crest and rounded bird body.
        val crest = Path().apply { moveTo(w*.25f,h*.27f); quadraticBezierTo(w*.16f,h*.04f,w*.38f,h*.20f); quadraticBezierTo(w*.43f,h*.05f,w*.52f,h*.22f); close() }
        drawPath(crest, emerald); drawPath(crest, navy, style = Stroke(w * .025f))
        drawOval(navy, topLeft = Offset(w*.17f,h*.22f), size = Size(w*.62f,h*.64f))
        drawOval(emerald, topLeft = Offset(w*.19f,h*.24f), size = Size(w*.58f,h*.60f))
        // Coral chest and turquoise wing.
        val chest = Path().apply { moveTo(w*.44f,h*.50f); quadraticBezierTo(w*.67f,h*.53f,w*.66f,h*.80f); quadraticBezierTo(w*.50f,h*.86f,w*.37f,h*.75f); quadraticBezierTo(w*.45f,h*.67f,w*.44f,h*.50f); close() }
        drawPath(chest, coral)
        val wing = Path().apply { moveTo(w*.27f,h*.49f); quadraticBezierTo(w*.47f,h*.42f,w*.56f,h*.59f); quadraticBezierTo(w*.48f,h*.71f,w*.27f,h*.68f); close() }
        drawPath(wing, teal); drawPath(wing, navy, style = Stroke(w*.02f))
        // Beak, eye and friendly cheek.
        val beak = Path().apply { moveTo(w*.76f,h*.40f); lineTo(w*.96f,h*.47f); lineTo(w*.75f,h*.54f); close() }
        drawPath(beak, Color(0xFFFFC66D)); drawPath(beak, navy, style = Stroke(w*.02f))
        drawCircle(navy, w*.035f, Offset(w*.66f,h*.38f)); drawCircle(Color.White, w*.012f, Offset(w*.67f,h*.37f))
        drawCircle(Color(0xFFFFA5A1), w*.035f, Offset(w*.72f,h*.50f))
    }
}
