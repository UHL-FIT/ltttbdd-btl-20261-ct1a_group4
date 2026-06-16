package com.example.studysync_btl.repository

import com.example.studysync_btl.data.Subject
import com.example.studysync_btl.data.SubjectDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudySyncRepository(
    private val subjectDao: SubjectDao
) {
    // Lấy tất cả subjects dạng Flow
    fun getAllSubjects(): Flow<List<Subject>> = subjectDao.getAllSubjects()

    // Lấy subjects theo ngày
    fun getSubjectsByDate(date: String): Flow<List<Subject>> = subjectDao.getSubjectsByDate(date)

    // Lấy các ngày có lịch (distinct)
    fun getDistinctDates(): Flow<List<String>> = subjectDao.getAllSubjects()
        .let { flow -> flow.map { list -> list.map { it.date }.distinct() } }

    // Lấy các môn thi
    fun getExamSubjects(): Flow<List<Subject>> = subjectDao.getExamSubjects()

    // Thêm một subject
    suspend fun addSubject(subject: Subject) = subjectDao.insertSubject(subject)

    // Thêm nhiều subjects
    suspend fun addSubjects(subjects: List<Subject>) = subjectDao.insertSubjects(subjects)

    // Xóa một subject
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    // Xóa tất cả subjects theo tên (chỉ lịch học)
    suspend fun deleteAllSubjectsByName(name: String) = subjectDao.deleteAllSubjectsByName(name)

    // Lấy subjects theo tên (đồng bộ)
    suspend fun getSubjectsByNameSync(name: String): List<Subject> = subjectDao.getSubjectsByNameSync(name)

    // Lấy subjects theo ngày (đồng bộ)
    suspend fun getSubjectsByDateSync(date: String): List<Subject> = subjectDao.getSubjectsByDateSync(date)
}