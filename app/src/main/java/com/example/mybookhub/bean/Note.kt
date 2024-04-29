package com.example.mybookhub.bean

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = LibraryBook::class,
            parentColumns = ["title", "author"],
            childColumns = ["bookTitle", "bookAuthor"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)

data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val bookTitle: String, // foreign key
    val bookAuthor: String, // foreign key
    val category: String,
    val content: String,
    val date: String
)
