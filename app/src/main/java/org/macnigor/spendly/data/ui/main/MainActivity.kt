package org.macnigor.spendly.data.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
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
import org.macnigor.spendly.data.viewmodel.MainViewModel
import org.macnigor.spendly.data.viewmodel.MainViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var balanceText: TextView
    private lateinit var purchaseDao: PurchaseDao
    private lateinit var incomeDao: IncomeDao
    private lateinit var viewModel: MainViewModel


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
        val factory = MainViewModelFactory(purchaseDao, incomeDao)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.balance.observe(this) { balance ->
            balanceText.text = "Баланс: %.2f ₽".format(Locale.US,balance)
        }

        val allCategories = mapOf(
            "Продукты" to R.id.foodAmount,
            "Транспорт" to R.id.transportAmount,
            "Аптека" to R.id.pharmacyAmount,
            "Одежда" to R.id.clothesAmount,
            "Хобби" to R.id.entertainmentAmount,
            "ЖКХ" to R.id.rentAmount,
            "Другое" to R.id.otherAmount
        )
        viewModel.categoryTotals.observe(this) { list ->
            // Превращаем list в карту для быстрого поиска
            val totalsMap = list.associateBy({ it.category }, { it.total })

            allCategories.forEach { (category, textViewId) ->
                val total = totalsMap[category] ?: 0.0
                findViewById<TextView>(textViewId).text = "%.0f ₽".format(Locale.US, total)
            }
        }




       /* viewModel.categoryTotals.observe(this) { list ->
            Log.d("MainActivity", "categoryTotals: $list")
            list.forEach { total ->
                val textViewId = when (total.category) {
                    "Продукты" -> R.id.foodAmount
                    "Транспорт" -> R.id.transportAmount
                    "Аптека" -> R.id.pharmacyAmount
                    "Одежда" -> R.id.clothesAmount
                    "Хобби" -> R.id.entertainmentAmount
                    "ЖКХ" -> R.id.rentAmount
                    "Другое" -> R.id.otherAmount
                    else -> null
                }

                textViewId?.let {
                    findViewById<TextView>(it).text = "%.0f ₽".format(Locale.US,total.total)
                }
            }
        }*/

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
