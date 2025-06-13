package org.macnigor.spendly.data.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.AppDatabase
import org.macnigor.spendly.data.database.IncomeDao
import org.macnigor.spendly.data.database.PurchaseDao
import org.macnigor.spendly.data.model.Purchase
import org.macnigor.spendly.data.utils.AddIncomeBottomSheet
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

        findViewById<MaterialButton>(R.id.incomeButton).setOnClickListener {
            val bottomSheet = AddIncomeBottomSheet { updateBalance() } // передаём коллбек
            bottomSheet.show(supportFragmentManager, "AddIncomeBottomSheet")
        }



        val categoryMap = mapOf(
            R.id.foodButton to "Продукты",
            R.id.transportButton to "Транспорт",
            R.id.pharmacyButton to "Аптека",
            R.id.clothesButton to "Одежда",
            R.id.entertainmentButton to "Хобби",
            R.id.rentButton to "ЖКХ",
            R.id.otherButton to "Другое"
        )


        lifecycleScope.launch {
            categoryMap.forEach { (id, category) ->
                val total = withContext(Dispatchers.IO) {
                    purchaseDao.getPurchasesByCategory(category).sumOf { it.amount }
                }

                val textViewId = when (id) {
                    R.id.foodButton -> R.id.foodAmount
                    R.id.transportButton -> R.id.transportAmount
                    R.id.pharmacyButton -> R.id.pharmacyAmount
                    R.id.clothesButton -> R.id.clothesAmount
                    R.id.entertainmentButton -> R.id.entertainmentAmount
                    R.id.rentButton -> R.id.rentAmount
                    R.id.otherButton -> R.id.otherAmount
                    else -> null
                }

                textViewId?.let {
                    findViewById<TextView>(it).text = "%.0f ₽".format(total)
                }
            }
        }



        updateBalance()
    }





    private fun updateBalance() {
        lifecycleScope.launch {
            val util = Utilities(purchaseDao, incomeDao)
            val balance = util.updateBalance()
            balanceText.text = "Баланс: %.2f ₽".format(Locale.US, balance)
        }
    }

    // функция срабатывает при переходе на этот экран
    override fun onResume() {
        super.onResume()
        updateBalance()
    }

    private fun onCategorySelected(category: String) {
        Toast.makeText(this, "Выбрана категория: $category", Toast.LENGTH_SHORT).show()
    }
}
