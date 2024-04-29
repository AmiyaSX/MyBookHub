package com.example.mybookhub.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category")
data class NoteCategory(
    @PrimaryKey
    var title: String,
    var description: String,

    ) : Serializable