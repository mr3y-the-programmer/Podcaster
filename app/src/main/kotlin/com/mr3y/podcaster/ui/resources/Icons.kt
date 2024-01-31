package com.mr3y.podcaster.ui.resources

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _Subscriptions: ImageVector? = null

public val Icons.Outlined.Subscriptions: ImageVector
    get() {
        if (_Subscriptions != null) {
            return _Subscriptions!!
        }
        _Subscriptions = ImageVector.Builder(
            name = "Subscriptions",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(3f, 3f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                verticalLineTo(3f)
                horizontalLineTo(3f)
                close()
                moveTo(9f, 9f)
                horizontalLineTo(5f)
                verticalLineTo(5f)
                horizontalLineToRelative(4f)
                verticalLineTo(9f)
                close()
                moveTo(3f, 13f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-8f)
                horizontalLineTo(3f)
                close()
                moveTo(9f, 19f)
                horizontalLineTo(5f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(4f)
                verticalLineTo(19f)
                close()
                moveTo(13f, 3f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                verticalLineTo(3f)
                horizontalLineTo(13f)
                close()
                moveTo(19f, 9f)
                horizontalLineToRelative(-4f)
                verticalLineTo(5f)
                horizontalLineToRelative(4f)
                verticalLineTo(9f)
                close()
                moveTo(13f, 13f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-8f)
                horizontalLineTo(13f)
                close()
                moveTo(19f, 19f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(4f)
                verticalLineTo(19f)
                close()
            }
        }.build()
        return _Subscriptions!!
    }
