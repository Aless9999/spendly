package org.macnigor.spendly.data.ui.common

import org.macnigor.spendly.data.database.dao.IncomeDao
import org.macnigor.spendly.data.database.dao.PurchaseDao

class Utilities(
    private val purchaseDao: PurchaseDao,
    private val incomeDao: IncomeDao
) {


    suspend fun updateBalance(): Double {
        val incomeVal = incomeDao.getAllIncome().sumOf { it.amount }
        val purchaseVal = purchaseDao.getAllPurchases().sumOf { it.amount }
        return incomeVal - purchaseVal
    }

}