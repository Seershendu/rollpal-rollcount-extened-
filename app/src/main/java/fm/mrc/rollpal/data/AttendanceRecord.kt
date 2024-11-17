package fm.mrc.rollpal.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val isPresent: Boolean,
    val semester: Int,
    val notes: String? = null
) 