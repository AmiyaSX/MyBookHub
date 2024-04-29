package com.example.mybookhub.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory

@Database(entities = [Note::class, LibraryBook::class, NoteCategory::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun libraryBookDao(): LibraryBookDao
    abstract fun noteCategoryDao(): NoteCategoryDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                context.getString(R.string.database_name)
            )
                .fallbackToDestructiveMigration()
                .build()

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }
    }
}