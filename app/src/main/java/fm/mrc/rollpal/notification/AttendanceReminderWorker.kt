package fm.mrc.rollpal.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AttendanceReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val notificationManager = AttendanceNotificationManager(context)
        notificationManager.showReminderNotification()
        Result.success()
    }

    companion object {
        const val WORK_NAME = "attendance_reminder_work"
    }
} 