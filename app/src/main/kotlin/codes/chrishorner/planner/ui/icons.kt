@file:Suppress("UnusedReceiverParameter") // Useful for structuring the API.

package codes.chrishorner.planner.ui


import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Shuffle: ImageVector
  get() {
    if (_shuffle != null) {
      return _shuffle!!
    }
    _shuffle = ImageVector.Builder(
      name = "Rounded.Shuffle",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f
    ).apply {
      path(
        fill = SolidColor(Color(0xFFE8EAED)),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(600f, 800f)
        quadToRelative(-17f, 0f, -28.5f, -11.5f)
        reflectiveQuadTo(560f, 760f)
        quadToRelative(0f, -17f, 11.5f, -28.5f)
        reflectiveQuadTo(600f, 720f)
        horizontalLineToRelative(64f)
        lineToRelative(-99f, -99f)
        quadToRelative(-12f, -12f, -11.5f, -28.5f)
        reflectiveQuadTo(566f, 564f)
        quadToRelative(12f, -12f, 28.5f, -12f)
        reflectiveQuadToRelative(28.5f, 12f)
        lineToRelative(97f, 98f)
        verticalLineToRelative(-62f)
        quadToRelative(0f, -17f, 11.5f, -28.5f)
        reflectiveQuadTo(760f, 560f)
        quadToRelative(17f, 0f, 28.5f, 11.5f)
        reflectiveQuadTo(800f, 600f)
        verticalLineToRelative(160f)
        quadToRelative(0f, 17f, -11.5f, 28.5f)
        reflectiveQuadTo(760f, 800f)
        horizontalLineTo(600f)
        close()
        moveToRelative(-428f, -12f)
        quadToRelative(-11f, -11f, -11f, -28f)
        reflectiveQuadToRelative(11f, -28f)
        lineToRelative(492f, -492f)
        horizontalLineToRelative(-64f)
        quadToRelative(-17f, 0f, -28.5f, -11.5f)
        reflectiveQuadTo(560f, 200f)
        quadToRelative(0f, -17f, 11.5f, -28.5f)
        reflectiveQuadTo(600f, 160f)
        horizontalLineToRelative(160f)
        quadToRelative(17f, 0f, 28.5f, 11.5f)
        reflectiveQuadTo(800f, 200f)
        verticalLineToRelative(160f)
        quadToRelative(0f, 17f, -11.5f, 28.5f)
        reflectiveQuadTo(760f, 400f)
        quadToRelative(-17f, 0f, -28.5f, -11.5f)
        reflectiveQuadTo(720f, 360f)
        verticalLineToRelative(-64f)
        lineTo(228f, 788f)
        quadToRelative(-11f, 11f, -28f, 11f)
        reflectiveQuadToRelative(-28f, -11f)
        close()
        moveToRelative(-1f, -560f)
        quadToRelative(-11f, -11f, -11f, -28f)
        reflectiveQuadToRelative(11f, -28f)
        quadToRelative(11f, -11f, 27.5f, -11f)
        reflectiveQuadToRelative(28.5f, 11f)
        lineToRelative(168f, 167f)
        quadToRelative(11f, 11f, 11.5f, 27.5f)
        reflectiveQuadTo(395f, 395f)
        quadToRelative(-11f, 11f, -28f, 11f)
        reflectiveQuadToRelative(-28f, -11f)
        lineTo(171f, 228f)
        close()
      }
    }.build()
    return _shuffle!!
  }

private var _shuffle: ImageVector? = null
