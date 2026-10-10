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
        // Two elegant tail feathers rise behind the body and taper downward.
        val leftTail = Path().apply {
            moveTo(w*.43f,h*.66f); quadraticBezierTo(w*.17f,h*.77f,w*.27f,h*.99f)
            quadraticBezierTo(w*.43f,h*.89f,w*.51f,h*.73f); close()
        }
        val rightTail = Path().apply {
            moveTo(w*.56f,h*.65f); quadraticBezierTo(w*.84f,h*.75f,w*.76f,h*.99f)
            quadraticBezierTo(w*.58f,h*.88f,w*.49f,h*.73f); close()
        }
        drawPath(leftTail, emerald); drawPath(leftTail, navy, style = Stroke(w*.018f))
        drawPath(rightTail, teal); drawPath(rightTail, navy, style = Stroke(w*.018f))

        // Symmetric rounded body and head make the bird read clearly from the front.
        drawOval(navy, topLeft = Offset(w*.17f,h*.31f), size = Size(w*.66f,h*.58f))
        drawOval(emerald, topLeft = Offset(w*.19f,h*.33f), size = Size(w*.62f,h*.54f))
        drawCircle(navy, w*.285f, Offset(w*.50f,h*.34f))
        drawCircle(emerald, w*.265f, Offset(w*.50f,h*.34f))
        // Turquoise crown sheen and small crest.
        drawArc(teal.copy(alpha=.9f), 205f, 130f, false, Offset(w*.27f,h*.08f), Size(w*.46f,h*.48f), style=Stroke(w*.035f))
        val crest = Path().apply { moveTo(w*.39f,h*.13f); quadraticBezierTo(w*.40f,h*.02f,w*.49f,h*.13f); quadraticBezierTo(w*.57f,h*.02f,w*.62f,h*.15f); close() }
        drawPath(crest, teal); drawPath(crest, navy, style=Stroke(w*.018f))

        // Broad, mirrored turquoise wings stay legible at compact sizes.
        val leftWing = Path().apply { moveTo(w*.24f,h*.52f); quadraticBezierTo(w*.08f,h*.55f,w*.23f,h*.77f); quadraticBezierTo(w*.38f,h*.74f,w*.43f,h*.60f); close() }
        val rightWing = Path().apply { moveTo(w*.76f,h*.52f); quadraticBezierTo(w*.92f,h*.55f,w*.77f,h*.77f); quadraticBezierTo(w*.62f,h*.74f,w*.57f,h*.60f); close() }
        drawPath(leftWing, teal); drawPath(leftWing, navy, style=Stroke(w*.018f))
        drawPath(rightWing, teal); drawPath(rightWing, navy, style=Stroke(w*.018f))
        // Coral breast centered beneath a small golden beak.
        drawOval(coral, topLeft=Offset(w*.39f,h*.57f), size=Size(w*.22f,h*.27f))
        val beak = Path().apply { moveTo(w*.46f,h*.40f); lineTo(w*.54f,h*.40f); lineTo(w*.50f,h*.49f); close() }
        drawPath(beak, Color(0xFFFFC66D)); drawPath(beak, navy, style=Stroke(w*.015f))
        // Large friendly eyes with highlights and gentle cheeks.
        listOf(.405f, .595f).forEach { x ->
            drawCircle(Color.White, w*.071f, Offset(w*x,h*.34f))
            drawCircle(navy, w*.038f, Offset(w*x,h*.345f))
            drawCircle(Color.White, w*.013f, Offset(w*(x-.012f),h*.33f))
        }
        drawCircle(Color(0xFFFFA5A1), w*.025f, Offset(w*.34f,h*.45f))
        drawCircle(Color(0xFFFFA5A1), w*.025f, Offset(w*.66f,h*.45f))
    }
}
