package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val title: String,
    val content: String,
    val mood: String? = null
)
