package com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield

import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import android.graphics.Path as AndroidPath
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.largePadding
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.log

@Composable
fun ZakarpattiaMap(modifier : Modifier,
    regionStatuses: Map<String, Double>, // Статуси для кожного регіону
    @DrawableRes vectorRes: Int, // Ресурс XML з картами регіонів
    onRegionClick: (String) -> Unit = {},// Колбек для обробки кліку по регіону
            scaleFactor: Float = 1.5f // Масштабування
) {
    val imageVector = ImageVector.vectorResource(id = vectorRes)

    Canvas(modifier = modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures { offset ->
            val clickedRegion = checkRegionClick(offset, imageVector) // Функція для перевірки області
            clickedRegion?.let {
                onRegionClick(it) // Викликаємо обробку кліку на регіоні
            }
        }
    }) {
        scale(scaleFactor, scaleFactor) {
            imageVector.root.forEach { node ->
                if (node is VectorPath && node.name != null) {
                    val regionId = node.name
                    val status = regionStatuses[regionId] ?: 0.0

                    // Визначаємо колір залежно від статусу
                    val fillColor = when {
                        status < 20 -> Color(0xFFFF0000) // Червоний
                        status < 40 -> Color(0xFFFFA500) // Помаранчевий
                        status < 60 -> Color(0xFFFFFF00) // Жовтий
                        status < 80 -> Color(0xFFADFF2F) // Світло-зелений
                        else -> Color(0xFF008000) // Зелений
                    }

                    // Малюємо шлях для цього регіону
                    drawPath(
                        path = toComposePath(node.pathData),
                        color = fillColor
                    )

                    // Обчислюємо межі регіону
                    val regionPath = toComposePath(node.pathData)
                    val bounds = RectF()
                    regionPath.asAndroidPath().computeBounds(bounds, true)

                    // Обчислюємо координати центру регіону
                    val x = bounds.centerX()
                    val y = bounds.centerY()

                    val strokePaint = Paint().apply {
                        color = android.graphics.Color.DKGRAY // Колір контуру
                        style = Paint.Style.STROKE
                        strokeWidth = 3f
                        textSize = 20f
                        textAlign = Paint.Align.CENTER
                    }

                    val fillPaint = Paint().apply {
                        color = android.graphics.Color.WHITE // Основний колір тексту
                        textSize = 20f
                        textAlign = Paint.Align.CENTER
                    }

                    // Малюємо текст із відсотками
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 22f
                            textAlign = Paint.Align.CENTER
                        }

                        // Визначаємо зміщення для вертикального центрування тексту
                        val metrics = paint.fontMetrics
                        val textOffsetY = (metrics.ascent + metrics.descent) / 2

                        drawText(
                            "${status.toInt()}%",
                            x , // Масштабуємо текстові координати
                            y  - textOffsetY - 10f,
                            strokePaint
                        )
                        drawText(
                            "${status.toInt()}%",
                            x , // Масштабуємо текстові координати
                            y  - textOffsetY - 10f,
                            fillPaint
                        )
                        // Малюємо region_id нижче відсотків
                        drawText(
                            regionId,
                            x,
                            y - textOffsetY +10, // Додаємо 25px відступ для region_id
                            strokePaint
                        )
                        drawText(
                            regionId,
                            x,
                            y - textOffsetY + 10, // Додаємо 25px відступ для region_id
                            fillPaint
                        )
                    }
                }
            }
        }
    }
}


//fun isPointInPath(path: Path, point: Offset): Boolean {
//    val androidPath = AndroidPath() // Створюємо Android Path
//    path.asAndroidPath() // Перетворюємо Compose Path в Android Path
//
//    // Створюємо PathMeasure для перевірки
//    val pathMeasure = PathMeasure(androidPath, false)
//
//    val length = pathMeasure.length
//    val tolerance = 0.5f // Точність перевірки
//
//    // Отримуємо координати точки на шляху
//    val pointOnPath = FloatArray(2)
//    pathMeasure.getPosTan(length / 2, pointOnPath, null)
//
//    val distance = Math.sqrt(
//        ((point.x - pointOnPath[0]) * (point.x - pointOnPath[0]) +
//                (point.y - pointOnPath[1]) * (point.y - pointOnPath[1])
//                ).toDouble()
//    )
//
//    return distance <= tolerance
//}

fun checkRegionClick(offset: Offset, imageVector: ImageVector): String? {
    // Перевіряємо для кожного регіону, чи потрапив клік в цей шлях
    imageVector.root.forEach { node ->
        if (node is VectorPath) {
            val regionPath = toComposePath(node.pathData) // Отримуємо шлях для регіону
            if (isPointInPath(regionPath, offset)) {
                return node.name // Якщо точка потрапила в цей шлях, повертаємо ID регіону
            }
        }
    }
    return null
}

// Перевірка, чи точка знаходиться всередині шляху
fun isPointInPath(path: Path, point: Offset): Boolean {
    val androidPath = AndroidPath()
    path.asAndroidPath() // Конвертуємо Compose Path в Android Path

    val pathMeasure = PathMeasure(androidPath, false)
    val length = pathMeasure.length
    val tolerance = 0.5f // Точність перевірки

    val pointOnPath = FloatArray(2)
    pathMeasure.getPosTan(length / 2, pointOnPath, null) // Перевірка середини шляху

    val distance = Math.sqrt(
        ((point.x - pointOnPath[0]) * (point.x - pointOnPath[0]) +
                (point.y - pointOnPath[1]) * (point.y - pointOnPath[1])
                ).toDouble()
    )

    return distance <= tolerance
}

fun toComposePath(pathData: List<PathNode>): Path {
    val path = Path()
    var currentX = 0f
    var currentY = 0f

    for (node in pathData) {
        when (node) {
            is PathNode.MoveTo -> {
                // Абсолютний MoveTo
                currentX = node.x
                currentY = node.y
                path.moveTo(currentX, currentY)
            }
            is PathNode.RelativeMoveTo -> {
                // Відносний MoveTo
                currentX += node.dx
                currentY += node.dy
                path.moveTo(currentX, currentY)
            }
            is PathNode.LineTo -> {
                // Абсолютний LineTo
                currentX = node.x
                currentY = node.y
                path.lineTo(currentX, currentY)
            }
            is PathNode.RelativeLineTo -> {
                // Відносний LineTo
                currentX += node.dx
                currentY += node.dy
                path.lineTo(currentX, currentY)
            }
            is PathNode.RelativeVerticalTo -> {
                currentY += node.dy
                path.lineTo(currentX, currentY)
            }
            // Обробка RelativeHorizontalTo (відносне горизонтальне переміщення)
            is PathNode.RelativeHorizontalTo -> {
                currentX += node.dx
                path.lineTo(currentX, currentY)
            }
            is PathNode.RelativeCurveTo -> {
                // Обробка відносних кривих (RelativeCurveTo)
                path.cubicTo(
                    currentX + node.dx1, currentY + node.dy1,
                    currentX + node.dx2, currentY + node.dy2,
                    currentX + node.dx3, currentY + node.dy3
                )
                // Оновлюємо поточні координати після кривої
                currentX += node.dx3
                currentY += node.dy3
            }
//            is PathNode.QuadTo -> {
//                // Абсолютний QuadTo
//                path.quadTo(node.x1, node.y1, node.x2, node.y2)
//            }
//            is PathNode.RelativeQuadTo -> {
//                // Відносний QuadTo
//                path.quadTo(currentX + node.dx1, currentY + node.dy1, currentX + node.dx2, currentY + node.dy2)
//            }
//            is PathNode.CubicTo -> {
//                // Абсолютний CubicTo
//                path.cubicTo(node.x1, node.y1, node.x2, node.y2, node.x3, node.y3)
//            }
//            is PathNode.RelativeCubicTo -> {
//                // Відносний CubicTo
//                path.cubicTo(currentX + node.dx1, currentY + node.dy1, currentX + node.dx2, currentY + node.dy2, currentX + node.dx3, currentY + node.dy3)
//            }
            is PathNode.Close -> {
                path.close()
            }
            else -> {
                // Логування або інше оброблення для непідтримуваних типів PathNode
                println("Unsupported path node: $node")
            }
        }
    }

    return path
}
//fun parseSvgToPaths(svgContent: String): Map<String, AndroidPath> {
//    val paths = mutableMapOf<String, AndroidPath>()
//    val parser = PathParser()
//    val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
//        .parse(InputSource(StringReader(svgContent)))
//
//    val elements = xmlDoc.getElementsByTagName("path")
//    for (i in 0 until elements.length) {
//        val element = elements.item(i) as Element
//        val id = element.getAttribute("id") ?: continue
//        val pathData = element.getAttribute("d") ?: continue
//        val path = parser.parsePathString(pathData).toPath()
//        paths[id] = path
//    }
//    return paths
//}
@Preview(showBackground = true)
@Composable
fun PreviewZakarpattiaMap() {
    // Mock дані для регіонів
    val mockStatuses = mapOf(
        stringResource(id = R.string.Uzhgorod) to 95.0, // 95% електроживлення
        stringResource(id = R.string.Mukachevo) to 95.0, // 95% електроживлення
        stringResource(id = R.string.Mizhhirskiy) to 30.0, // 60% електроживлення
        stringResource(id = R.string.Svalyava) to 30.0, // 60% електроживлення
        stringResource(id = R.string.Tyachiv) to 30.0, // 60% електроживлення
        stringResource(id = R.string.Irshava) to 30.0, // 60% електроживлення
        stringResource(id = R.string.Beregovo) to 30.0, // 60% електроживлення
        stringResource(id = R.string.Khust) to 60.0, // 60% електроживлення
        stringResource(id = R.string.Volovets) to 60.0, // 60% електроживлення
        stringResource(id = R.string.Rahiv) to 60.0, // 60% електроживлення
        stringResource(id = R.string.Vinogradiv) to 60.0, // 60% електроживлення
        stringResource(id = R.string.Bereznuy) to 60.0, // 60% електроживлення
        stringResource(id = R.string.Perechyn) to 30.0  // 30% електроживлення
    )

    ZakarpattiaMap(modifier = Modifier,
        regionStatuses = mockStatuses,
        vectorRes = R.drawable.zakarpattia_regions, // Ваш файл XML
        { Log.d("ZakarpattiaMap","Click $it") }
    )
}
@Preview(showBackground = true)
@Composable
fun SimplePreview() {
    Text("Hello, Preview!")
}

//@Composable
//fun loadSvgFromDrawable(@DrawableRes svgRes: Int): String {
//    val context = LocalContext.current
//    return context.resources.openRawResource(svgRes).bufferedReader().use { it.readText() }
//}