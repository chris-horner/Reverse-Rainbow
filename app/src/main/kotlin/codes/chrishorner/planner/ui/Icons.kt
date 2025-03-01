@file:Suppress("UnusedReceiverParameter") // Useful for structuring the API.

package codes.chrishorner.planner.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
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

  val ArrowBack: ImageVector
    get() {
      if (_ArrowBack != null) {
        return _ArrowBack!!
      }
      _ArrowBack = ImageVector.Builder(
        name = "Rounded.ArrowBack",
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
          moveTo(313f, 520f)
          lineToRelative(196f, 196f)
          quadToRelative(12f, 12f, 11.5f, 28f)
          reflectiveQuadTo(508f, 772f)
          quadToRelative(-12f, 11f, -28f, 11.5f)
          reflectiveQuadTo(452f, 772f)
          lineTo(188f, 508f)
          quadToRelative(-6f, -6f, -8.5f, -13f)
          reflectiveQuadToRelative(-2.5f, -15f)
          quadToRelative(0f, -8f, 2.5f, -15f)
          reflectiveQuadToRelative(8.5f, -13f)
          lineToRelative(264f, -264f)
          quadToRelative(11f, -11f, 27.5f, -11f)
          reflectiveQuadToRelative(28.5f, 11f)
          quadToRelative(12f, 12f, 12f, 28.5f)
          reflectiveQuadTo(508f, 245f)
          lineTo(313f, 440f)
          horizontalLineToRelative(447f)
          quadToRelative(17f, 0f, 28.5f, 11.5f)
          reflectiveQuadTo(800f, 480f)
          quadToRelative(0f, 17f, -11.5f, 28.5f)
          reflectiveQuadTo(760f, 520f)
          horizontalLineTo(313f)
          close()
        }
      }.build()
      return _ArrowBack!!
    }

  private var _ArrowBack: ImageVector? = null

  val Person: ImageVector
    get() {
      if (_Person != null) {
        return _Person!!
      }
      _Person = ImageVector.Builder(
        name = "Rounded.Person",
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
          moveTo(480f, 480f)
          quadToRelative(-66f, 0f, -113f, -47f)
          reflectiveQuadToRelative(-47f, -113f)
          quadToRelative(0f, -66f, 47f, -113f)
          reflectiveQuadToRelative(113f, -47f)
          quadToRelative(66f, 0f, 113f, 47f)
          reflectiveQuadToRelative(47f, 113f)
          quadToRelative(0f, 66f, -47f, 113f)
          reflectiveQuadToRelative(-113f, 47f)
          close()
          moveTo(160f, 720f)
          verticalLineToRelative(-32f)
          quadToRelative(0f, -34f, 17.5f, -62.5f)
          reflectiveQuadTo(224f, 582f)
          quadToRelative(62f, -31f, 126f, -46.5f)
          reflectiveQuadTo(480f, 520f)
          quadToRelative(66f, 0f, 130f, 15.5f)
          reflectiveQuadTo(736f, 582f)
          quadToRelative(29f, 15f, 46.5f, 43.5f)
          reflectiveQuadTo(800f, 688f)
          verticalLineToRelative(32f)
          quadToRelative(0f, 33f, -23.5f, 56.5f)
          reflectiveQuadTo(720f, 800f)
          horizontalLineTo(240f)
          quadToRelative(-33f, 0f, -56.5f, -23.5f)
          reflectiveQuadTo(160f, 720f)
          close()
        }
      }.build()
      return _Person!!
    }

  private var _Person: ImageVector? = null

  val Warning: ImageVector
    get() {
      if (_Warning != null) {
        return _Warning!!
      }
      _Warning = ImageVector.Builder(
        name = "Warning_24dp_E3E3E3_FILL1_wght400_GRAD0_opsz24",
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
          moveTo(109f, 840f)
          quadToRelative(-11f, 0f, -20f, -5.5f)
          reflectiveQuadTo(75f, 820f)
          quadToRelative(-5f, -9f, -5.5f, -19.5f)
          reflectiveQuadTo(75f, 780f)
          lineToRelative(370f, -640f)
          quadToRelative(6f, -10f, 15.5f, -15f)
          reflectiveQuadToRelative(19.5f, -5f)
          quadToRelative(10f, 0f, 19.5f, 5f)
          reflectiveQuadToRelative(15.5f, 15f)
          lineToRelative(370f, 640f)
          quadToRelative(6f, 10f, 5.5f, 20.5f)
          reflectiveQuadTo(885f, 820f)
          quadToRelative(-5f, 9f, -14f, 14.5f)
          reflectiveQuadToRelative(-20f, 5.5f)
          horizontalLineTo(109f)
          close()
          moveToRelative(371f, -120f)
          quadToRelative(17f, 0f, 28.5f, -11.5f)
          reflectiveQuadTo(520f, 680f)
          quadToRelative(0f, -17f, -11.5f, -28.5f)
          reflectiveQuadTo(480f, 640f)
          quadToRelative(-17f, 0f, -28.5f, 11.5f)
          reflectiveQuadTo(440f, 680f)
          quadToRelative(0f, 17f, 11.5f, 28.5f)
          reflectiveQuadTo(480f, 720f)
          close()
          moveToRelative(0f, -120f)
          quadToRelative(17f, 0f, 28.5f, -11.5f)
          reflectiveQuadTo(520f, 560f)
          verticalLineToRelative(-120f)
          quadToRelative(0f, -17f, -11.5f, -28.5f)
          reflectiveQuadTo(480f, 400f)
          quadToRelative(-17f, 0f, -28.5f, 11.5f)
          reflectiveQuadTo(440f, 440f)
          verticalLineToRelative(120f)
          quadToRelative(0f, 17f, 11.5f, 28.5f)
          reflectiveQuadTo(480f, 600f)
          close()
        }
      }.build()
      return _Warning!!
    }

  private var _Warning: ImageVector? = null

  val Block: ImageVector
    get() {
      if (_Block != null) {
        return _Block!!
      }
      _Block = ImageVector.Builder(
        name = "Rounded.Block",
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
          moveTo(480f, 880f)
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
          moveToRelative(0f, -80f)
          quadToRelative(54f, 0f, 104f, -17.5f)
          reflectiveQuadToRelative(92f, -50.5f)
          lineTo(228f, 284f)
          quadToRelative(-33f, 42f, -50.5f, 92f)
          reflectiveQuadTo(160f, 480f)
          quadToRelative(0f, 134f, 93f, 227f)
          reflectiveQuadToRelative(227f, 93f)
          close()
          moveToRelative(252f, -124f)
          quadToRelative(33f, -42f, 50.5f, -92f)
          reflectiveQuadTo(800f, 480f)
          quadToRelative(0f, -134f, -93f, -227f)
          reflectiveQuadToRelative(-227f, -93f)
          quadToRelative(-54f, 0f, -104f, 17.5f)
          reflectiveQuadTo(284f, 228f)
          lineToRelative(448f, 448f)
          close()
        }
      }.build()
      return _Block!!
    }

  private var _Block: ImageVector? = null

  val GitHub: ImageVector
    get() {
      if (_gitHub != null) {
        return _gitHub!!
      }
      _gitHub = Builder(
        name = "GitHub",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
      ).apply {
        path(
          fill = SolidColor(Color(0xFFE3E3E3)), stroke = null, strokeLineWidth = 0.0f,
          strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(12.0f, 2.0f)
          arcTo(10.0f, 10.0f, 0.0f, false, false, 2.0f, 12.0f)
          curveToRelative(0.0f, 4.42f, 2.87f, 8.17f, 6.84f, 9.5f)
          curveToRelative(0.5f, 0.08f, 0.66f, -0.23f, 0.66f, -0.5f)
          verticalLineToRelative(-1.69f)
          curveToRelative(-2.77f, 0.6f, -3.36f, -1.34f, -3.36f, -1.34f)
          curveToRelative(-0.46f, -1.16f, -1.11f, -1.47f, -1.11f, -1.47f)
          curveToRelative(-0.91f, -0.62f, 0.07f, -0.6f, 0.07f, -0.6f)
          curveToRelative(1.0f, 0.07f, 1.53f, 1.03f, 1.53f, 1.03f)
          curveToRelative(0.87f, 1.52f, 2.34f, 1.07f, 2.91f, 0.83f)
          curveToRelative(0.09f, -0.65f, 0.35f, -1.09f, 0.63f, -1.34f)
          curveToRelative(-2.22f, -0.25f, -4.55f, -1.11f, -4.55f, -4.92f)
          curveToRelative(0.0f, -1.11f, 0.38f, -2.0f, 1.03f, -2.71f)
          curveToRelative(-0.1f, -0.25f, -0.45f, -1.29f, 0.1f, -2.64f)
          curveToRelative(0.0f, 0.0f, 0.84f, -0.27f, 2.75f, 1.02f)
          curveToRelative(0.79f, -0.22f, 1.65f, -0.33f, 2.5f, -0.33f)
          reflectiveCurveToRelative(1.71f, 0.11f, 2.5f, 0.33f)
          curveToRelative(1.91f, -1.29f, 2.75f, -1.02f, 2.75f, -1.02f)
          curveToRelative(0.55f, 1.35f, 0.2f, 2.39f, 0.1f, 2.64f)
          curveToRelative(0.65f, 0.71f, 1.03f, 1.6f, 1.03f, 2.71f)
          curveToRelative(0.0f, 3.82f, -2.34f, 4.66f, -4.57f, 4.91f)
          curveToRelative(0.36f, 0.31f, 0.69f, 0.92f, 0.69f, 1.85f)
          verticalLineTo(21.0f)
          curveToRelative(0.0f, 0.27f, 0.16f, 0.59f, 0.67f, 0.5f)
          curveTo(19.14f, 20.16f, 22.0f, 16.42f, 22.0f, 12.0f)
          arcTo(10.0f, 10.0f, 0.0f, false, false, 12.0f, 2.0f)
        }
      }
        .build()
      return _gitHub!!
    }

  private var _gitHub: ImageVector? = null
}
