package fm.mrc.rollpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fm.mrc.rollpal.viewmodel.AttendanceViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = viewModel()
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.Default.KeyboardArrowLeft, "Previous month")
            }
            
            Text(
                text = currentMonth.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )
            
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.Default.KeyboardArrowRight, "Next month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of week header
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DayOfWeek.values()) { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Add empty spaces for days before the first day of month
            val firstDayOfMonth = currentMonth.atDay(1)
            val emptyDays = firstDayOfMonth.dayOfWeek.value % 7
            items(emptyDays) {
                Box(modifier = Modifier.padding(4.dp))
            }

            // Add days of the month
            items(currentMonth.lengthOfMonth()) { day ->
                val date = currentMonth.atDay(day + 1)
                val isSelected = date == selectedDate
                val record = attendanceRecords.find { it.date == date }

                CalendarDay(
                    date = date,
                    isSelected = isSelected,
                    attendance = record?.isPresent,
                    onDateClick = { viewModel.setSelectedDate(date) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Attendance buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.markAttendance(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Present")
            }
            
            Button(
                onClick = { viewModel.markAttendance(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Absent")
            }
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    attendance: Boolean?,
    onDateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    attendance == true -> Color(0xFF81C784)
                    attendance == false -> Color(0xFFE57373)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onDateClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground
        )
    }
} 