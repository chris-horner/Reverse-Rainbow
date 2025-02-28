@file:Suppress("UnusedReceiverParameter") // Useful for structuring the API.

package codes.chrishorner.planner.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object Icons {

  val Shuffle: ImageVector
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

  val Info: ImageVector
    get() {
      if (_Info != null) {
        return _Info!!
      }
      _Info = ImageVector.Builder(
        name = "Rounded.Info",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
      ).apply {
        path(
          fill = SolidColor(Color(0xFFE3E3E3)),
          fillAlpha = 1.0f,
          stroke = null,
          strokeAlpha = 1.0f,
          strokeLineWidth = 1.0f,
          strokeLineCap = StrokeCap.Butt,
          strokeLineJoin = StrokeJoin.Miter,
          strokeLineMiter = 1.0f,
          pathFillType = PathFillType.NonZero
        ) {
          moveTo(480f, 680f)
          quadToRelative(17f, 0f, 28.5f, -11.5f)
          reflectiveQuadTo(520f, 640f)
          verticalLineToRelative(-160f)
          quadToRelative(0f, -17f, -11.5f, -28.5f)
          reflectiveQuadTo(480f, 440f)
          quadToRelative(-17f, 0f, -28.5f, 11.5f)
          reflectiveQuadTo(440f, 480f)
          verticalLineToRelative(160f)
          quadToRelative(0f, 17f, 11.5f, 28.5f)
          reflectiveQuadTo(480f, 680f)
          close()
          moveToRelative(0f, -320f)
          quadToRelative(17f, 0f, 28.5f, -11.5f)
          reflectiveQuadTo(520f, 320f)
          quadToRelative(0f, -17f, -11.5f, -28.5f)
          reflectiveQuadTo(480f, 280f)
          quadToRelative(-17f, 0f, -28.5f, 11.5f)
          reflectiveQuadTo(440f, 320f)
          quadToRelative(0f, 17f, 11.5f, 28.5f)
          reflectiveQuadTo(480f, 360f)
          close()
          moveToRelative(0f, 520f)
          quadToRelative(-83f, 0f, -156f, -31.5f)
          reflectiveQuadTo(197f, 763f)
          quadToRelative(-54f, -54f, -85.5f, -127f)
          reflectiveQuadTo(80f, 480f)
          quadToRelative(0f, -83f, 31.5f, -156f)
          reflectiveQuadTo(197f, 197f)
          quadToRelative(54f, -54f, 127f, -85.5f)
          reflectiveQuadTo(480f, 80f)
          quadToRelative(83f, 0f, 156f, 31.5f)
          reflectiveQuadTo(763f, 197f)
          quadToRelative(54f, 54f, 85.5f, 127f)
          reflectiveQuadTo(880f, 480f)
          quadToRelative(0f, 83f, -31.5f, 156f)
          reflectiveQuadTo(763f, 763f)
          quadToRelative(-54f, 54f, -127f, 85.5f)
          reflectiveQuadTo(480f, 880f)
          close()
        }
      }.build()
      return _Info!!
    }

  private var _Info: ImageVector? = null

  val Clear: ImageVector
    get() {
      if (_Clear != null) {
        return _Clear!!
      }
      _Clear = ImageVector.Builder(
        name = "Rounded.Clear",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
      ).apply {
        path(
          fill = SolidColor(Color(0xFFE3E3E3)),
          fillAlpha = 1.0f,
          stroke = null,
          strokeAlpha = 1.0f,
          strokeLineWidth = 1.0f,
          strokeLineCap = StrokeCap.Butt,
          strokeLineJoin = StrokeJoin.Miter,
          strokeLineMiter = 1.0f,
          pathFillType = PathFillType.NonZero
        ) {
          moveTo(480f, 536f)
          lineTo(284f, 732f)
          quadToRelative(-11f, 11f, -28f, 11f)
          reflectiveQuadToRelative(-28f, -11f)
          quadToRelative(-11f, -11f, -11f, -28f)
          reflectiveQuadToRelative(11f, -28f)
          lineToRelative(196f, -196f)
          lineToRelative(-196f, -196f)
          quadToRelative(-11f, -11f, -11f, -28f)
          reflectiveQuadToRelative(11f, -28f)
          quadToRelative(11f, -11f, 28f, -11f)
          reflectiveQuadToRelative(28f, 11f)
          lineToRelative(196f, 196f)
          lineToRelative(196f, -196f)
          quadToRelative(11f, -11f, 28f, -11f)
          reflectiveQuadToRelative(28f, 11f)
          quadToRelative(11f, 11f, 11f, 28f)
          reflectiveQuadToRelative(-11f, 28f)
          lineTo(536f, 480f)
          lineToRelative(196f, 196f)
          quadToRelative(11f, 11f, 11f, 28f)
          reflectiveQuadToRelative(-11f, 28f)
          quadToRelative(-11f, 11f, -28f, 11f)
          reflectiveQuadToRelative(-28f, -11f)
          lineTo(480f, 536f)
          close()
        }
      }.build()
      return _Clear!!
    }

  private var _Clear: ImageVector? = null

  val MoreVert: ImageVector
    get() {
      if (_MoreVert != null) {
        return _MoreVert!!
      }
      _MoreVert = ImageVector.Builder(
        name = "Rounded.MoreVert",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
      ).apply {
        path(
          fill = SolidColor(Color(0xFFE3E3E3)),
          fillAlpha = 1.0f,
          stroke = null,
          strokeAlpha = 1.0f,
          strokeLineWidth = 1.0f,
          strokeLineCap = StrokeCap.Butt,
          strokeLineJoin = StrokeJoin.Miter,
          strokeLineMiter = 1.0f,
          pathFillType = PathFillType.NonZero
        ) {
          moveTo(480f, 800f)
          quadToRelative(-33f, 0f, -56.5f, -23.5f)
          reflectiveQuadTo(400f, 720f)
          quadToRelative(0f, -33f, 23.5f, -56.5f)
          reflectiveQuadTo(480f, 640f)
          quadToRelative(33f, 0f, 56.5f, 23.5f)
          reflectiveQuadTo(560f, 720f)
          quadToRelative(0f, 33f, -23.5f, 56.5f)
          reflectiveQuadTo(480f, 800f)
          close()
          moveToRelative(0f, -240f)
          quadToRelative(-33f, 0f, -56.5f, -23.5f)
          reflectiveQuadTo(400f, 480f)
          quadToRelative(0f, -33f, 23.5f, -56.5f)
          reflectiveQuadTo(480f, 400f)
          quadToRelative(33f, 0f, 56.5f, 23.5f)
          reflectiveQuadTo(560f, 480f)
          quadToRelative(0f, 33f, -23.5f, 56.5f)
          reflectiveQuadTo(480f, 560f)
          close()
          moveToRelative(0f, -240f)
          quadToRelative(-33f, 0f, -56.5f, -23.5f)
          reflectiveQuadTo(400f, 240f)
          quadToRelative(0f, -33f, 23.5f, -56.5f)
          reflectiveQuadTo(480f, 160f)
          quadToRelative(33f, 0f, 56.5f, 23.5f)
          reflectiveQuadTo(560f, 240f)
          quadToRelative(0f, 33f, -23.5f, 56.5f)
          reflectiveQuadTo(480f, 320f)
          close()
        }
      }.build()
      return _MoreVert!!
    }

  private var _MoreVert: ImageVector? = null
}
