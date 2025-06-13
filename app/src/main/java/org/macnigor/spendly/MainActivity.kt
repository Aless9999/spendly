package org.macnigor.spendly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.macnigor.spendly.data.database.AppDatabase
import org.macnigor.spendly.data.database.IncomeDao
import org.macnigor.spendly.data.database.PurchaseDao
import org.macnigor.spendly.data.utils.Utilities
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var balanceText: TextView
    private lateinit var purchaseDao: PurchaseDao
    private lateinit var incomeDao: IncomeDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        balanceText = findViewById(R.id.balanceText)

        purchaseDao = AppDatabase.getDatabase(this).purchaseDao()
        incomeDao = AppDatabase.getDatabase(this).incomeDao()

        updateBalance()
    }

    private fun updateBalance() {
        lifecycleScope.launch {
            val util = Utilities(purchaseDao, incomeDao)
            val balance = util.updateBalance()
            balanceText.text = "%.2f ₽".format(Locale.US, balance)
        }
    }
}