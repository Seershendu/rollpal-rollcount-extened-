package fm.mrc.rollpal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fm.mrc.rollpal.viewmodel.StatsViewModel
import kotlin.math.min

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val currentSemester by viewModel.currentSemester.collectAsState()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Attendance Statistics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Semester selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Semester $currentSemester")
            Row {
                TextButton(
                    onClick = { if (currentSemester > 1) viewModel.setSemester(currentSemester - 1) }
                ) {
                    Text("Previous")
                }
                TextButton(
                    onClick = { if (currentSemester < 3) viewModel.setSemester(currentSemester + 1) }
                ) {
                    Text("Next")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Stats Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quick Stats",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                StatRow("Total Classes", stats.totalClasses.toString())
                StatRow("Present", stats.present.toString())
                StatRow("Absent", stats.absent.toString())
                StatRow("Current Attendance", "%.1f%%".format(stats.percentage))
                StatRow("Current Streak", "${stats.streak} days")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Attendance Analysis Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Attendance Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (stats.percentage < 75.0) {
                    Text(
                        text = "Classes needed for 75%: ${stats.classesNeededFor75}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = "Projected Attendance: %.1f%%".format(stats.projectedAttendance),
                    color = if (stats.projectedAttendance >= 75.0) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monthly Attendance Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Monthly Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Bar Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val barWidth = size.width / (stats.monthlyAttendance.size + 1)
                        val maxHeight = size.height - 20.dp.toPx()

                        stats.monthlyAttendance.forEachIndexed { index, data ->
                            val barHeight = (data.percentage.toFloat() / 100f) * maxHeight
                            val x = barWidth * (index + 0.5f)
                            
                            // Draw bar
                            drawRect(
                                color = if (data.percentage >= 75f) 
                                    Color(0xFF81C784) else Color(0xFFE57373),
                                topLeft = Offset(x, size.height - barHeight),
                                size = Size(barWidth * 0.8f, barHeight)
                            )
                            
                            // Draw month label
                            with(density) {
                                drawContext.canvas.nativeCanvas.drawText(
                                    data.month.take(3),
                                    x,
                                    size.height,
                                    android.graphics.Paint().apply {
                                        textSize = 12.dp.toPx()
                                        color = android.graphics.Color.BLACK
                                        textAlign = android.graphics.Paint.Align.CENTER
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
} 