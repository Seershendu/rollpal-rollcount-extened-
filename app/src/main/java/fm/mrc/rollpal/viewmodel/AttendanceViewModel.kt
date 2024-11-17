package fm.mrc.rollpal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import fm.mrc.rollpal.data.AttendanceDatabase
import fm.mrc.rollpal.data.AttendanceRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        AttendanceDatabase::class.java,
        "attendance_db"
    ).build()

    private val dao = database.attendanceDao()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _currentSemester = MutableStateFlow(1)
    val currentSemester: StateFlow<Int> = _currentSemester

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords

    init {
        loadAttendanceForCurrentMonth()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setSemester(semester: Int) {
        _currentSemester.value = semester
        loadAttendanceForCurrentMonth()
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadAttendanceForCurrentMonth()
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadAttendanceForCurrentMonth()
    }

    private fun loadAttendanceForCurrentMonth() {
        viewModelScope.launch {
            val startDate = _currentMonth.value.atDay(1)
            val endDate = _currentMonth.value.atEndOfMonth()
            dao.getAttendanceByDateRange(startDate, endDate).collect {
                _attendanceRecords.value = it
            }
        }
    }

    fun markAttendance(isPresent: Boolean) {
        viewModelScope.launch {
            val record = AttendanceRecord(
                date = _selectedDate.value,
                isPresent = isPresent,
                semester = _currentSemester.value
            )
            dao.insertAttendance(record)
        }
    }

    fun getAttendanceStats() = viewModelScope.launch {
        dao.getAttendanceBySemester(_currentSemester.value).collect { records ->
            val total = records.size
            val present = records.count { it.isPresent }
            val percentage = if (total > 0) (present * 100.0 / total) else 0.0
            // Update stats in UI
        }
    }
} 