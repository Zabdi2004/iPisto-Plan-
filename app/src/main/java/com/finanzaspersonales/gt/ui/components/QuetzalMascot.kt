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

/** Original, proportion-preserving quetzal illustration drawn locally for offline use. */
@Composable
fun QuetzalMascot(modifier: Modifier = Modifier, size: Dp = 76.dp) {
    val emerald = Color(0xFF36A980)
    val lightEmerald = Color(0xFF63C8A2)
    val teal = Color(0xFF55C7D4)
    val coral = Color(0xFFEF817E)
    val deepCoral = Color(0xFFD96370)
    val navy = Color(0xFF202942)

    Canvas(modifier.size(size).semantics { contentDescription = "Quetzal verde de iPisto" }) {
        val w = this.size.width
        val h = this.size.height
        val outline = w * .012f

        // Two long, tapered tail plumes frame the body and remain inside the square viewport.
        val leftTail = Path().apply {
            moveTo(w * .43f, h * .61f)
            cubicTo(w * .31f, h * .69f, w * .19f, h * .82f, w * .24f, h * .99f)
            cubicTo(w * .34f, h * .91f, w * .44f, h * .78f, w * .50f, h * .70f)
            close()
        }
        val rightTail = Path().apply {
            moveTo(w * .57f, h * .61f)
            cubicTo(w * .69f, h * .69f, w * .81f, h * .82f, w * .76f, h * .99f)
            cubicTo(w * .66f, h * .91f, w * .56f, h * .78f, w * .50f, h * .70f)
            close()
        }
        drawPath(leftTail, emerald)
        drawPath(rightTail, teal)
        drawPath(leftTail, navy, style = Stroke(outline))
        drawPath(rightTail, navy, style = Stroke(outline))
        drawLine(teal.copy(alpha = .75f), Offset(w * .42f, h * .72f), Offset(w * .29f, h * .94f), outline)
        drawLine(lightEmerald, Offset(w * .57f, h * .72f), Offset(w * .72f, h * .94f), outline)

        // One continuous, subtly waisted torso silhouette; the head overlaps its shoulders.
        val torso = Path().apply {
            moveTo(w * .34f, h * .36f)
            cubicTo(w * .25f, h * .41f, w * .20f, h * .57f, w * .25f, h * .73f)
            cubicTo(w * .29f, h * .87f, w * .40f, h * .91f, w * .50f, h * .91f)
            cubicTo(w * .60f, h * .91f, w * .71f, h * .87f, w * .75f, h * .73f)
            cubicTo(w * .80f, h * .57f, w * .75f, h * .41f, w * .66f, h * .36f)
            cubicTo(w * .58f, h * .31f, w * .42f, h * .31f, w * .34f, h * .36f)
            close()
        }
        drawPath(torso, emerald)
        drawPath(torso, navy, style = Stroke(outline))

        // Wings follow the torso contour, with only a few broad feather cuts for small sizes.
        val leftWing = Path().apply {
            moveTo(w * .31f, h * .46f)
            cubicTo(w * .20f, h * .47f, w * .16f, h * .60f, w * .25f, h * .75f)
            cubicTo(w * .34f, h * .70f, w * .38f, h * .58f, w * .41f, h * .49f)
            close()
        }
        val rightWing = Path().apply {
            moveTo(w * .69f, h * .46f)
            cubicTo(w * .80f, h * .47f, w * .84f, h * .60f, w * .75f, h * .75f)
            cubicTo(w * .66f, h * .70f, w * .62f, h * .58f, w * .59f, h * .49f)
            close()
        }
        drawPath(leftWing, lightEmerald)
        drawPath(rightWing, teal)
        drawPath(leftWing, navy, style = Stroke(outline))
        drawPath(rightWing, navy, style = Stroke(outline))
        drawLine(emerald, Offset(w * .28f, h * .53f), Offset(w * .35f, h * .66f), outline)
        drawLine(emerald, Offset(w * .72f, h * .53f), Offset(w * .65f, h * .66f), outline)

        // The coral bib is a feather-shaped area nested into the torso, not a detached oval.
        val breast = Path().apply {
            moveTo(w * .40f, h * .50f)
            cubicTo(w * .43f, h * .55f, w * .46f, h * .57f, w * .50f, h * .58f)
            cubicTo(w * .54f, h * .57f, w * .57f, h * .55f, w * .60f, h * .50f)
            cubicTo(w * .61f, h * .59f, w * .62f, h * .70f, w * .58f, h * .82f)
            cubicTo(w * .55f, h * .88f, w * .53f, h * .89f, w * .50f, h * .90f)
            cubicTo(w * .47f, h * .89f, w * .45f, h * .88f, w * .42f, h * .82f)
            cubicTo(w * .38f, h * .70f, w * .39f, h * .59f, w * .40f, h * .50f)
            close()
        }
        drawPath(breast, coral)
        drawPath(breast, deepCoral.copy(alpha = .6f), style = Stroke(outline * .65f))

        // Rounded head and crest connect naturally at the shoulders; the teal crown is a sheen.
        drawOval(navy, topLeft = Offset(w * .245f, h * .075f), size = Size(w * .51f, h * .43f))
        drawOval(emerald, topLeft = Offset(w * .255f, h * .085f), size = Size(w * .49f, h * .41f))
        val crownSheen = Path().apply {
            moveTo(w * .31f, h * .22f)
            cubicTo(w * .34f, h * .11f, w * .44f, h * .08f, w * .50f, h * .105f)
            cubicTo(w * .43f, h * .14f, w * .39f, h * .18f, w * .37f, h * .27f)
            close()
        }
        drawPath(crownSheen, teal)
        val crest = Path().apply {
            moveTo(w * .39f, h * .105f)
            cubicTo(w * .38f, h * .035f, w * .46f, h * .045f, w * .49f, h * .11f)
            cubicTo(w * .54f, h * .035f, w * .61f, h * .055f, w * .60f, h * .13f)
            cubicTo(w * .54f, h * .16f, w * .45f, h * .16f, w * .39f, h * .105f)
            close()
        }
        drawPath(crest, teal)

        // Small, calm eyes and a defined golden bill keep the face recognizable at 32 dp.
        listOf(.405f, .595f).forEach { x ->
            drawCircle(navy, w * .028f, Offset(w * x, h * .315f))
            drawCircle(Color.White.copy(alpha = .88f), w * .008f, Offset(w * (x - .008f), h * .307f))
        }
        val beak = Path().apply {
            moveTo(w * .465f, h * .34f)
            cubicTo(w * .48f, h * .33f, w * .52f, h * .33f, w * .535f, h * .34f)
            lineTo(w * .50f, h * .405f)
            close()
        }
        drawPath(beak, Color(0xFFFFC66D))
        drawPath(beak, navy, style = Stroke(outline * .7f))
    }
}
