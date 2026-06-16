package com.example.studysync_btl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync_btl.data.AppDatabase
import com.example.studysync_btl.data.Subject
import com.example.studysync_btl.repository.StudySyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

class StudySyncViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudySyncRepository

    init {
        val dao = AppDatabase.getDatabase(application).subjectDao()
        repository = StudySyncRepository(dao)
    }

    // Lấy danh sách môn học theo ngày (Tự động cập nhật khi DB thay đổi)
    fun getSubjectsByDate(date: String): Flow<List<Subject>> {
        return repository.getSubjectsByDate(date)
    }

    val cacngaycolich: StateFlow<List<String>> = repository.getDistinctDates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lấy danh sách môn thi
    val examSubjects: Flow<List<Subject>> = repository.getExamSubjects()

    // Chuyển sang StateFlow để có thể truy cập giá trị hiện tại qua .value
    val allSubjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Hàm kiểm tra trùng lặp
    fun checkDuplicate(name: String, date: String, startTime: String, excludeId: Int = 0): Boolean {
        val currentList = allSubjects.value
        return currentList.any {
            it.id != excludeId &&
                    it.name.equals(name, ignoreCase = true) &&
                    it.date == date &&
                    it.time.startsWith(startTime)
        }
    }
    fun validateSubjectInput(
        name: String,
        date: String,
        endDate: String?,
        startTime: String,
        endTime: String,
        room: String,
        isExam: Boolean,
        credits: Int,
        isEditing: Boolean,
        currentSubjectId: Int = 0
    ): String? {
        // 1. Kiểm tra trống và định dạng tên
        if (name.isBlank()) return "Tên môn học không được để trống"
        if (name.toDoubleOrNull() != null) {
            return if (name.toDouble() < 0) "Tên môn học không được là số âm" 
            else "Tên môn học không được chỉ bao gồm chữ số"
        }
        if (date.isBlank()) return "Ngày học không được để trống"
        if (startTime.isBlank()) return "Giờ bắt đầu không được để trống"
        if (room.isBlank()) return "Phòng học không được để trống"

        // Kiểm tra số tín chỉ (nếu không phải lịch thi)
        if (!isExam && (credits < 1 || credits > 3)) {
            return "Số tín chỉ phải từ 1 đến 3"
        }

        // Kiểm tra ngày kết thúc cho lịch học định kỳ
        if (!isExam && endDate.isNullOrBlank()) {
            return "Vui lòng chọn ngày kết thúc cho lịch học"
        }

        // 2. Kiểm tra định dạng ngày (dd/MM/yyyy)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        val parsedDate = try {
            dateFormat.parse(date)
        } catch (e: Exception) {
            return "Ngày không hợp lệ (dd/MM/yyyy)"
        }
        if (parsedDate == null) return "Ngày không hợp lệ"
        if (!isExam && endDate != null) {
            try {
                val parsedEndDate = dateFormat.parse(endDate)
                if (parsedEndDate != null && parsedEndDate.before(parsedDate)) {
                    return "Ngày kết thúc không được trước ngày bắt đầu"
                }
            } catch (e: Exception) {
                return "Ngày kết thúc không hợp lệ"
            }
        }

        // 3. Kiểm tra giờ bắt đầu và kết thúc (định dạng HH:mm)
        val timePattern = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
        if (!timePattern.matches(startTime)) return "Giờ bắt đầu không đúng định dạng HH:mm"
        if (endTime.isNotBlank() && endTime != "00:00" && !timePattern.matches(endTime)) return "Giờ kết thúc không đúng định dạng HH:mm"

        // 4. So sánh giờ bắt đầu và kết thúc (nếu có)
        if (endTime.isNotBlank() && endTime != "00:00") {
            val startMinutes = startTime.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            val endMinutes = endTime.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            if (endMinutes <= startMinutes) return "Giờ kết thúc phải sau giờ bắt đầu"
        }

        // 5. Kiểm tra trùng lặp
        if (isExam) {
            val exists = allSubjects.value.any { 
                it.isExam && it.name.trim().equals(name.trim(), ignoreCase = true) && it.id != (if (isEditing) currentSubjectId else 0)
            }
            if (exists) return "Môn thi này đã tồn tại"
        }

        if (checkDuplicate(name, date, startTime, if (isEditing) currentSubjectId else 0)) {
            return "Môn học này đã tồn tại trong cùng ngày và giờ"
        }

        return null
    }
    // Hàm xóa tất cả lịch của một môn học theo tên
    fun deleteAllSubjectsByName(name: String) {
        viewModelScope.launch {
            repository.deleteAllSubjectsByName(name)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // Cập nhật chuỗi môn học hàng tuần (Xóa cũ thêm mới trong 1 Transaction)
    fun updateWeeklySubject(oldName: String, newSubjects: List<Subject>) {
        viewModelScope.launch {
            repository.deleteAllSubjectsByName(oldName)
            repository.addSubjects(newSubjects)
        }
    }

    // Hàm thêm hoặc cập nhật môn học (đồng bộ)
    suspend fun addSubjectSync(subject: Subject) {
        repository.addSubject(subject)
    }

    // Hàm thêm hoặc cập nhật môn học
    fun addSubject(subject: Subject) {
        viewModelScope.launch {
            repository.addSubject(subject)
        }
    }

    // Lấy danh sách môn học theo tên (đồng bộ)
    suspend fun getSubjectsByName(name: String): List<Subject> {
        return repository.getSubjectsByNameSync(name)
    }

    // Thêm nhiều môn học cùng lúc (đồng bộ)
    suspend fun addSubjectsSync(subjects: List<Subject>) {
        repository.addSubjects(subjects)
    }

    // Thêm nhiều môn học cùng lúc (tối ưu hơn vòng lặp)
    fun addSubjects(subjects: List<Subject>) {
        viewModelScope.launch {
            repository.addSubjects(subjects)
        }
    }


    fun tinhNgayConLai(ngayThi: String): Int {
        return try {
            val dinhDangNgay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateThi = dinhDangNgay.parse(ngayThi)
            val homNay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            val diff = (dateThi?.time ?: 0L) - homNay.time
            (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }
}
