package org.macnigor.spendly.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.macnigor.spendly.data.model.Income
import org.macnigor.spendly.data.model.Purchase

@Database(entities = [Purchase::class, Income::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun purchaseDao(): PurchaseDao
    abstract fun incomeDao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun clearDatabase() {
            INSTANCE?.clearAllTables()
        }
    }
}