package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.JournalDao
import com.example.untitled.data.local.entities.JournalEntity
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {
    val allEntries: Flow<List<JournalEntity>> = journalDao.getAllEntries()

    suspend fun insertEntry(entry: JournalEntity) {
        journalDao.insertEntry(entry)
    }
}
