package org.macnigor.spendly.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.macnigor.spendly.data.model.CategoryTotal
import org.macnigor.spendly.data.model.Purchase

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: Purchase)

    @Delete
    suspend fun delete(purchase: Purchase)

    @Query("SELECT * FROM purchases ORDER BY date DESC")
    suspend fun getAllPurchases(): List<Purchase>

    @Query("SELECT category, SUM(amount) as total FROM purchases GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    @Query("SELECT * FROM purchases WHERE category = :category")
    fun getPurchasesByCategory(category: String): List<Purchase>


}