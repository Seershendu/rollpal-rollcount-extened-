package fm.mrc.rollpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fm.mrc.rollpal.viewmodel.SemesterViewModel

@Composable
fun SemesterScreen(
    viewModel: SemesterViewModel = viewModel()
) {
    val currentSemester by viewModel.currentSemester.collectAsState()
    val semesterInfo by viewModel.semesterInfo.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Semester Management",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Current Semester Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Current Semester",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Semester $currentSemester")
                    Row {
                        TextButton(
                            onClick = { viewModel.previousSemester() },
                            enabled = currentSemester > 1
                        ) {
                            Text("Previous")
                        }
                        TextButton(
                            onClick = { viewModel.nextSemester() },
                            enabled = currentSemester < 8
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Semester Settings Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Semester Settings",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Start Date
                Text(
                    text = "Start Date: ${semesterInfo.startDate}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* Show date picker */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Change Start Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // End Date
                Text(
                    text = "End Date: ${semesterInfo.endDate}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* Show date picker */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Change End Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Attendance Goal
                OutlinedTextField(
                    value = semesterInfo.attendanceGoal.toString(),
                    onValueChange = { 
                        it.toIntOrNull()?.let { goal ->
                            viewModel.updateAttendanceGoal(goal)
                        }
                    },
                    label = { Text("Attendance Goal (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.resetSemesterData() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset Semester Data")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.backupSemesterData() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Backup Data")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.restoreSemesterData() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Data")
                }
            }
        }
    }
} 