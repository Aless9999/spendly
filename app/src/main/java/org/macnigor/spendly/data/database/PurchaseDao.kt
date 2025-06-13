package org.macnigor.spendly.data.database

import androidx.room.*
import org.macnigor.spendly.data.model.Purchase

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: Purchase)

    @Delete
    suspend fun delete(purchase: Purchase)

    @Query("SELECT * FROM purchases ORDER BY date DESC")
    suspend fun getAllPurchases(): List<Purchase>
}