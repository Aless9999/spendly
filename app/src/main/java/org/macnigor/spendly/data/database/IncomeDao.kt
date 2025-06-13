package org.macnigor.spendly.data.database

import androidx.room.*
import org.macnigor.spendly.data.model.Income

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: Income)

    @Delete
    suspend fun delete(income: Income)

    @Query("SELECT * FROM income ORDER BY date DESC")
    suspend fun getAllIncome(): List<Income>
}