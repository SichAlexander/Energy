package com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PowerStatusGraph(data: Map<String, List<Int>>) {
    // Розміри графіка
    val graphWidth = 400f
    val graphHeight = 300f

    // Максимальне значення для нормалізації
    val maxValue = data.values.flatten().maxOrNull() ?: 1

    // Малюємо Canvas
    Canvas(modifier = Modifier.size(graphWidth.dp, graphHeight.dp)) {
        // Осі координат
        drawLine(
            color = Color.Black,
            start = Offset(50f, size.height - 50f),
            end = Offset(size.width - 50f, size.height - 50f), // X-вісь
            strokeWidth = 4f
        )
        drawLine(
            color = Color.Black,
            start = Offset(50f, size.height - 50f),
            end = Offset(50f, 50f), // Y-вісь
            strokeWidth = 4f
        )

        // Малюємо точки та лінії графіка
        val regionColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta)
        var colorIndex = 0
        data.forEach { (region, values) ->
            val stepX = (size.width - 100f) / values.size // Відстань між точками
            val points = values.mapIndexed { index, value ->
                Offset(
                    x = 50f + stepX * index,
                    y = size.height - 50f - (value / maxValue.toFloat()) * (size.height - 100f)
                )
            }
            val regionColor = regionColors[colorIndex % regionColors.size]
            colorIndex++

            // Лінії між точками
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = regionColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 4f
                )
            }

            // Точки
            points.forEach { point ->
                drawCircle(color = regionColor, center = point, radius = 8f)
            }
        }
    }
}

@Composable
fun PowerStatusScreen() {
    // Дані: регіон -> список точок за 15 хвилин
    val data = mapOf(
        "Region 1" to listOf(2, 4, 3, 5, 2),
        "Region 2" to listOf(1, 3, 2, 4, 1),
        "Region 3" to listOf(0, 2, 3, 1, 0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Power Status Graph",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )
        PowerStatusGraph(data = data)
    }
}

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            PowerStatusScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}
