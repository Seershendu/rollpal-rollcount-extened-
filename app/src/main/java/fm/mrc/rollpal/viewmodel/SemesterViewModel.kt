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

data class SemesterInfo(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusMonths(6),
    val attendanceGoal: Int = 75
)

class SemesterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        AttendanceDatabase::class.java,
        "attendance_db"
    ).build()

    private val dao = database.attendanceDao()

    private val _currentSemester = MutableStateFlow(1)
    val currentSemester: StateFlow<Int> = _currentSemester

    private val _semesterInfo = MutableStateFlow(SemesterInfo())
    val semesterInfo: StateFlow<SemesterInfo> = _semesterInfo

    fun nextSemester() {
        if (_currentSemester.value < 8) {
            _currentSemester.value += 1
            loadSemesterInfo()
        }
    }

    fun previousSemester() {
        if (_currentSemester.value > 1) {
            _currentSemester.value -= 1
            loadSemesterInfo()
        }
    }

    fun updateAttendanceGoal(goal: Int) {
        _semesterInfo.value = _semesterInfo.value.copy(attendanceGoal = goal)
        saveSemesterInfo()
    }

    fun updateStartDate(date: LocalDate) {
        _semesterInfo.value = _semesterInfo.value.copy(startDate = date)
        saveSemesterInfo()
    }

    fun updateEndDate(date: LocalDate) {
        _semesterInfo.value = _semesterInfo.value.copy(endDate = date)
        saveSemesterInfo()
    }

    fun resetSemesterData() {
        viewModelScope.launch {
            // Delete all attendance records for current semester
            // This is a placeholder - implement actual deletion logic
        }
    }

    fun backupSemesterData() {
        viewModelScope.launch {
            // Implement backup logic
        }
    }

    fun restoreSemesterData() {
        viewModelScope.launch {
            // Implement restore logic
        }
    }

    private fun loadSemesterInfo() {
        // Load semester info from preferences or database
        // This is a placeholder - implement actual loading logic
    }

    private fun saveSemesterInfo() {
        viewModelScope.launch {
            // Save semester info to preferences or database
            // This is a placeholder - implement actual saving logic
        }
    }
} 