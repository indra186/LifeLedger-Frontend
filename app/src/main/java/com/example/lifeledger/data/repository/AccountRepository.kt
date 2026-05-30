package com.example.lifeledger.data.repository

import com.example.lifeledger.data.local.dao.AccountDao
import com.example.lifeledger.data.local.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val accountDao: AccountDao) {
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()

    suspend fun insertAccount(account: AccountEntity) {
        accountDao.insertAccount(account)
    }
}
