package com.example.studysync_btl.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    // 1. Lấy toàn bộ danh sách môn học (Dùng Flow để tự động cập nhật giao diện khi dữ liệu thay đổi)
    @Query("SELECT * FROM subjects ORDER BY id DESC")
    fun getAllSubjects(): Flow<List<Subject>>

    // 2. Thêm một môn mới (Nếu trùng ID thì ghi đè lên)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    // 3. Thêm danh sách môn học (Dùng cho tính năng lặp lại theo tuần)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    // 4. Xóa một môn
    @Delete
    suspend fun deleteSubject(subject: Subject)

    // Xóa tất cả lịch học của một môn cụ thể theo tên (không xóa lịch thi)
    @Query("DELETE FROM subjects WHERE name = :name AND isExam = 0")
    suspend fun deleteAllSubjectsByName(name: String)

    // Lấy danh sách môn học theo tên (không dùng Flow)
    @Query("SELECT * FROM subjects WHERE name = :name AND isExam = 0")
    suspend fun getSubjectsByNameSync(name: String): List<Subject>

    // 5. Tìm môn học theo ngày (Phục vụ trang Lịch học)
    @Query("SELECT * FROM subjects WHERE date = :date AND isExam = 0")
    fun getSubjectsByDate(date: String): Flow<List<Subject>>

    // 6. Lấy danh sách môn thi (isExam = 1)
    @Query("SELECT * FROM subjects WHERE isExam = 1")
    fun getExamSubjects(): Flow<List<Subject>>

    // 7. Lấy danh sách môn học theo ngày đồng bộ (không dùng Flow)
    @Query("SELECT * FROM subjects WHERE date = :date")
    suspend fun getSubjectsByDateSync(date: String): List<Subject>
}
