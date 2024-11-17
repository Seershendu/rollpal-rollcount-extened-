package fm.mrc.rollpal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import fm.mrc.rollpal.data.AttendanceDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.YearMonth

data class AttendanceStats(
    val totalClasses: Int = 0,
    val present: Int = 0,
    val absent: Int = 0,
    val percentage: Double = 0.0,
    val streak: Int = 0,
    val monthlyStats: Map<String, Double> = emptyMap(),
    val monthlyAttendance: List<MonthlyAttendance> = emptyList(),
    val requiredAttendance: Double = 75.0, // Minimum required attendance
    val classesNeededFor75: Int = 0,  // Classes needed to reach 75%
    val projectedAttendance: Double = 0.0 // Projected attendance at current rate
)

data class MonthlyAttendance(
    val month: String,
    val present: Int,
    val total: Int,
    val percentage: Double
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        AttendanceDatabase::class.java,
        "attendance_db"
    ).build()

    private val dao = database.attendanceDao()

    private val _currentSemester = MutableStateFlow(1)
    val currentSemester: StateFlow<Int> = _currentSemester

    private val _stats = MutableStateFlow(AttendanceStats())
    val stats: StateFlow<AttendanceStats> = _stats

    init {
        loadStats()
    }

    fun setSemester(semester: Int) {
        _currentSemester.value = semester
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            dao.getAttendanceBySemester(_currentSemester.value).collect { records ->
                val total = records.size
                val present = records.count { it.isPresent }
                val absent = total - present
                val percentage = if (total > 0) (present * 100.0 / total) else 0.0

                // Calculate streak
                val sortedRecords = records.sortedByDescending { it.date }
                var streak = 0
                var currentDate = LocalDate.now()

                for (record in sortedRecords) {
                    if (record.isPresent && ChronoUnit.DAYS.between(record.date, currentDate) <= 1) {
                        streak++
                        currentDate = record.date
                    } else {
                        break
                    }
                }

                // Calculate monthly statistics
                val monthlyAttendance = records
                    .groupBy { YearMonth.from(it.date) }
                    .map { (month, monthRecords) ->
                        val monthPresent = monthRecords.count { it.isPresent }
                        val monthTotal = monthRecords.size
                        val monthPercentage = if (monthTotal > 0) (monthPresent * 100.0 / monthTotal) else 0.0
                        
                        MonthlyAttendance(
                            month = month.month.toString(),
                            present = monthPresent,
                            total = monthTotal,
                            percentage = monthPercentage
                        )
                    }
                    .sortedBy { it.month }

                // Calculate classes needed for 75%
                val classesNeededFor75 = if (percentage < 75.0) {
                    val x = (3 * total - 4 * present) / 1
                    if (x < 0) 0 else x.toInt()
                } else 0

                // Calculate projected attendance
                val projectedAttendance = if (total > 0) {
                    val rate = present.toDouble() / total
                    rate * 100
                } else 0.0

                _stats.value = AttendanceStats(
                    totalClasses = total,
                    present = present,
                    absent = absent,
                    percentage = percentage,
                    streak = streak,
                    monthlyStats = monthlyAttendance.associate { it.month to it.percentage },
                    monthlyAttendance = monthlyAttendance,
                    classesNeededFor75 = classesNeededFor75,
                    projectedAttendance = projectedAttendance
                )
            }
        }
    }
} 