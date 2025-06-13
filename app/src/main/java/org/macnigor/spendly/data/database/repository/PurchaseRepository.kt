package org.macnigor.spendly.data.database.repository

import androidx.lifecycle.LiveData
import org.macnigor.spendly.data.database.dao.PurchaseDao
import org.macnigor.spendly.data.model.CategoryTotal
import org.macnigor.spendly.data.model.Purchase

class PurchaseRepository(private val purchaseDao: PurchaseDao) {

    fun getCategoryTotals(): LiveData<List<CategoryTotal>> {
        return purchaseDao.getCategoryTotals()
    }

    suspend fun getAllPurchases(): List<Purchase> {
        return purchaseDao.getAllPurchases()
    }

    suspend fun insertPurchase(purchase: Purchase) {
        purchaseDao.insert(purchase)
    }
}
