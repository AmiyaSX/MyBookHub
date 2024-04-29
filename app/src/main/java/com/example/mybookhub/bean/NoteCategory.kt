package com.example.mybookhub.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category")
data class NoteCategory(
    @PrimaryKey
    val title: String,
    val description: String,

) : Serializable