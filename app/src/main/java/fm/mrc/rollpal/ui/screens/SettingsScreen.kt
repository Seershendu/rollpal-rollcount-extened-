package fm.mrc.rollpal.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.*
import fm.mrc.rollpal.notification.AttendanceReminderWorker
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.lifecycle.viewmodel.compose.viewModel
import fm.mrc.rollpal.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    var enableReminders by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Reminders")
                    Switch(
                        checked = enableReminders,
                        onCheckedChange = { enabled ->
                            enableReminders = enabled
                            if (enabled) {
                                scheduleReminder(context, reminderTime)
                            } else {
                                cancelReminder(context)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme"
                        )
                        Text(if (isDarkMode) "Dark Theme" else "Light Theme")
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Roll Pal",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    text = "Version 1.0 (alpha)",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Developer: Seershendu",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Roll Pal is an attendance tracking application designed to help students manage their attendance efficiently across multiple semesters.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun scheduleReminder(context: Context, time: LocalTime) {
    val workManager = WorkManager.getInstance(context)
    
    val reminderRequest = PeriodicWorkRequestBuilder<AttendanceReminderWorker>(
        24, TimeUnit.HOURS
    ).build()

    workManager.enqueueUniquePeriodicWork(
        AttendanceReminderWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        reminderRequest
    )
}

private fun cancelReminder(context: Context) {
    WorkManager.getInstance(context)
        .cancelUniqueWork(AttendanceReminderWorker.WORK_NAME)
} 