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

  val Construction: ImageVector
    get() {
      if (_Construction != null) {
        return _Construction!!
      }
      _Construction = ImageVector.Builder(
        name = "Rounded.Construction",
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
          moveTo(714f, 798f)
          lineTo(537f, 621f)
          lineToRelative(84f, -84f)
          lineToRelative(177f, 177f)
          quadToRelative(17f, 17f, 17f, 42f)
          reflectiveQuadToRelative(-17f, 42f)
          quadToRelative(-17f, 17f, -42f, 17f)
          reflectiveQuadToRelative(-42f, -17f)
          close()
          moveToRelative(-552f, 0f)
          quadToRelative(-17f, -17f, -17f, -42f)
          reflectiveQuadToRelative(17f, -42f)
          lineToRelative(234f, -234f)
          lineToRelative(-68f, -68f)
          quadToRelative(-11f, 11f, -28f, 11f)
          reflectiveQuadToRelative(-28f, -11f)
          lineToRelative(-23f, -23f)
          verticalLineToRelative(90f)
          quadToRelative(0f, 14f, -12f, 19f)
          reflectiveQuadToRelative(-22f, -5f)
          lineTo(106f, 384f)
          quadToRelative(-10f, -10f, -5f, -22f)
          reflectiveQuadToRelative(19f, -12f)
          horizontalLineToRelative(90f)
          lineToRelative(-22f, -22f)
          quadToRelative(-12f, -12f, -12f, -28f)
          reflectiveQuadToRelative(12f, -28f)
          lineToRelative(114f, -114f)
          quadToRelative(20f, -20f, 43f, -29f)
          reflectiveQuadToRelative(47f, -9f)
          quadToRelative(20f, 0f, 37.5f, 6f)
          reflectiveQuadToRelative(34.5f, 18f)
          quadToRelative(8f, 5f, 8.5f, 14f)
          reflectiveQuadToRelative(-6.5f, 16f)
          lineToRelative(-76f, 76f)
          lineToRelative(22f, 22f)
          quadToRelative(11f, 11f, 11f, 28f)
          reflectiveQuadToRelative(-11f, 28f)
          lineToRelative(68f, 68f)
          lineToRelative(90f, -90f)
          quadToRelative(-4f, -11f, -6.5f, -23f)
          reflectiveQuadToRelative(-2.5f, -24f)
          quadToRelative(0f, -59f, 40.5f, -99.5f)
          reflectiveQuadTo(701f, 119f)
          quadToRelative(8f, 0f, 15f, 0.5f)
          reflectiveQuadToRelative(14f, 2.5f)
          quadToRelative(9f, 3f, 11.5f, 12.5f)
          reflectiveQuadTo(737f, 151f)
          lineToRelative(-65f, 65f)
          quadToRelative(-6f, 6f, -6f, 14f)
          reflectiveQuadToRelative(6f, 14f)
          lineToRelative(44f, 44f)
          quadToRelative(6f, 6f, 14f, 6f)
          reflectiveQuadToRelative(14f, -6f)
          lineToRelative(65f, -65f)
          quadToRelative(7f, -7f, 16.5f, -5f)
          reflectiveQuadToRelative(12.5f, 12f)
          quadToRelative(2f, 7f, 2.5f, 14f)
          reflectiveQuadToRelative(0.5f, 15f)
          quadToRelative(0f, 59f, -40.5f, 99.5f)
          reflectiveQuadTo(701f, 399f)
          quadToRelative(-12f, 0f, -24f, -2f)
          reflectiveQuadToRelative(-23f, -7f)
          lineTo(246f, 798f)
          quadToRelative(-17f, 17f, -42f, 17f)
          reflectiveQuadToRelative(-42f, -17f)
          close()
        }
      }.build()
      return _Construction!!
    }

  private var _Construction: ImageVector? = null

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

  val GitHub: ImageVector
    get() {
      if (_gitHub != null) {
        return _gitHub!!
      }
      _gitHub = ImageVector.Builder(
        name = "GitHub",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
      ).apply {
        path(
          fill = SolidColor(Color(0xFFE3E3E3)),
          stroke = null,
          strokeLineWidth = 0.0f,
          strokeLineCap = StrokeCap.Butt,
          strokeLineJoin = StrokeJoin.Miter,
          strokeLineMiter = 4.0f,
          pathFillType = PathFillType.NonZero
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

  val EditNote: ImageVector
    get() {
      if (_EditNote != null) {
        return _EditNote!!
      }
      _EditNote = ImageVector.Builder(
        name = "Rounded.EditNote",
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
          moveTo(200f, 560f)
          quadToRelative(-17f, 0f, -28.5f, -11.5f)
          reflectiveQuadTo(160f, 520f)
          quadToRelative(0f, -17f, 11.5f, -28.5f)
          reflectiveQuadTo(200f, 480f)
          horizontalLineToRelative(200f)
          quadToRelative(17f, 0f, 28.5f, 11.5f)
          reflectiveQuadTo(440f, 520f)
          quadToRelative(0f, 17f, -11.5f, 28.5f)
          reflectiveQuadTo(400f, 560f)
          horizontalLineTo(200f)
          close()
          moveToRelative(0f, -160f)
          quadToRelative(-17f, 0f, -28.5f, -11.5f)
          reflectiveQuadTo(160f, 360f)
          quadToRelative(0f, -17f, 11.5f, -28.5f)
          reflectiveQuadTo(200f, 320f)
          horizontalLineToRelative(360f)
          quadToRelative(17f, 0f, 28.5f, 11.5f)
          reflectiveQuadTo(600f, 360f)
          quadToRelative(0f, 17f, -11.5f, 28.5f)
          reflectiveQuadTo(560f, 400f)
          horizontalLineTo(200f)
          close()
          moveToRelative(0f, -160f)
          quadToRelative(-17f, 0f, -28.5f, -11.5f)
          reflectiveQuadTo(160f, 200f)
          quadToRelative(0f, -17f, 11.5f, -28.5f)
          reflectiveQuadTo(200f, 160f)
          horizontalLineToRelative(360f)
          quadToRelative(17f, 0f, 28.5f, 11.5f)
          reflectiveQuadTo(600f, 200f)
          quadToRelative(0f, 17f, -11.5f, 28.5f)
          reflectiveQuadTo(560f, 240f)
          horizontalLineTo(200f)
          close()
          moveToRelative(320f, 520f)
          verticalLineToRelative(-66f)
          quadToRelative(0f, -8f, 3f, -15.5f)
          reflectiveQuadToRelative(9f, -13.5f)
          lineToRelative(209f, -208f)
          quadToRelative(9f, -9f, 20f, -13f)
          reflectiveQuadToRelative(22f, -4f)
          quadToRelative(12f, 0f, 23f, 4.5f)
          reflectiveQuadToRelative(20f, 13.5f)
          lineToRelative(37f, 37f)
          quadToRelative(8f, 9f, 12.5f, 20f)
          reflectiveQuadToRelative(4.5f, 22f)
          quadToRelative(0f, 11f, -4f, 22.5f)
          reflectiveQuadTo(863f, 580f)
          lineTo(655f, 788f)
          quadToRelative(-6f, 6f, -13.5f, 9f)
          reflectiveQuadToRelative(-15.5f, 3f)
          horizontalLineToRelative(-66f)
          quadToRelative(-17f, 0f, -28.5f, -11.5f)
          reflectiveQuadTo(520f, 760f)
          close()
          moveToRelative(263f, -184f)
          lineToRelative(37f, -39f)
          lineToRelative(-37f, -37f)
          lineToRelative(-38f, 38f)
          lineToRelative(38f, 38f)
          close()
          moveTo(580f, 740f)
          horizontalLineToRelative(38f)
          lineToRelative(121f, -122f)
          lineToRelative(-18f, -19f)
          lineToRelative(-19f, -18f)
          lineToRelative(-122f, 121f)
          verticalLineToRelative(38f)
          close()
          moveToRelative(0f, 0f)
          verticalLineToRelative(-38f)
          lineToRelative(122f, -121f)
          lineToRelative(37f, 37f)
          lineToRelative(-121f, 122f)
          horizontalLineToRelative(-38f)
          close()
        }
      }.build()
      return _EditNote!!
    }

  private var _EditNote: ImageVector? = null

  val DeleteSweep: ImageVector
    get() {
      if (_DeleteSweep != null) {
        return _DeleteSweep!!
      }
      _DeleteSweep = ImageVector.Builder(
        name = "Rounded.DeleteSweep",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 960.0f,
        viewportHeight = 960.0f
      ).apply {
        path(
          fill = SolidColor(Color(0xFFE3E3E3)),
          stroke = null,
          strokeLineWidth = 0.0f,
          strokeLineCap = StrokeCap.Butt,
          strokeLineJoin = StrokeJoin.Miter,
          strokeLineMiter = 1.0f,
          pathFillType = PathFillType.NonZero
        ) {
          moveTo(200.0f, 760.0f)
          quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
          reflectiveQuadTo(120.0f, 680.0f)
          verticalLineToRelative(-360.0f)
          quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
          reflectiveQuadTo(80.0f, 280.0f)
          quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
          reflectiveQuadTo(120.0f, 240.0f)
          horizontalLineToRelative(120.0f)
          verticalLineToRelative(-20.0f)
          quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
          reflectiveQuadTo(280.0f, 180.0f)
          horizontalLineToRelative(80.0f)
          quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
          reflectiveQuadTo(400.0f, 220.0f)
          verticalLineToRelative(20.0f)
          horizontalLineToRelative(120.0f)
          quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
          reflectiveQuadTo(560.0f, 280.0f)
          quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
          reflectiveQuadTo(520.0f, 320.0f)
          verticalLineToRelative(360.0f)
          quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
          reflectiveQuadTo(440.0f, 760.0f)
          lineTo(200.0f, 760.0f)
          close()
          moveTo(640.0f, 720.0f)
          quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
          reflectiveQuadTo(600.0f, 680.0f)
          quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
          reflectiveQuadTo(640.0f, 640.0f)
          horizontalLineToRelative(80.0f)
          quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
          reflectiveQuadTo(760.0f, 680.0f)
          quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
          reflectiveQuadTo(720.0f, 720.0f)
          horizontalLineToRelative(-80.0f)
          close()
          moveTo(640.0f, 560.0f)
          quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
          reflectiveQuadTo(600.0f, 520.0f)
          quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
          reflectiveQuadTo(640.0f, 480.0f)
          horizontalLineToRelative(160.0f)
          quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
          reflectiveQuadTo(840.0f, 520.0f)
          quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
          reflectiveQuadTo(800.0f, 560.0f)
          lineTo(640.0f, 560.0f)
          close()
          moveTo(640.0f, 400.0f)
          quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
          reflectiveQuadTo(600.0f, 360.0f)
          quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
          reflectiveQuadTo(640.0f, 320.0f)
          horizontalLineToRelative(200.0f)
          quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
          reflectiveQuadTo(880.0f, 360.0f)
          quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
          reflectiveQuadTo(840.0f, 400.0f)
          lineTo(640.0f, 400.0f)
          close()
        }
      }
        .build()
      return _DeleteSweep!!
    }

  private var _DeleteSweep: ImageVector? = null
}
