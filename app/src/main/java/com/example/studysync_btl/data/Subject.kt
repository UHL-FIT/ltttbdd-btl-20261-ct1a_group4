package com.example.studysync_btl.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects") // Đánh dấu đây là một bảng tên là "subjects"
data class Subject(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,          // ID tự động tăng, không cần nhập
    val name: String,         // Tên môn học
    val room: String,         // Phòng học/Phòng thi
    val time: String,         // Giờ học (VD: 07:00 - 09:00)
    val date: String,         // Ngày học/Ngày thi (Dạng dd/MM/yyyy)
    val note: String = "",    // Ghi chú thêm
    val isExam: Boolean = false, // true = Lịch thi, false = Lịch học
    val credits: Int = 0      // Số tín chỉ
)
