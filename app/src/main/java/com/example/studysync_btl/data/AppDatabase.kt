package com.example.studysync_btl.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Subject::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    // Khai báo để lấy được các lệnh từ DAO
    abstract fun subjectDao(): SubjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Hàm để khởi tạo Database (Dùng chung cho toàn App)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studysync_database" // Tên file database lưu trong máy
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
