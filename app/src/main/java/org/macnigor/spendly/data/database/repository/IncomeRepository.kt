package org.macnigor.spendly.data.database.repository

import org.macnigor.spendly.data.database.dao.IncomeDao
import org.macnigor.spendly.data.model.Income

class IncomeRepository(private val incomeDao: IncomeDao) {

    suspend fun getAllIncomes(): List<Income> {
        return incomeDao.getAllIncome()
    }

    suspend fun insertIncome(income: Income) {
        incomeDao.insert(income)
    }
}
