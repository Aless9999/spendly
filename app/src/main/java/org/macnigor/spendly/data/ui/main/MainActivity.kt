package org.macnigor.spendly.data.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.db.AppDatabase
import org.macnigor.spendly.data.database.dao.IncomeDao
import org.macnigor.spendly.data.database.dao.PurchaseDao
import org.macnigor.spendly.data.ui.common.AddIncomeBottomSheet
import org.macnigor.spendly.data.ui.common.Utilities
import org.macnigor.spendly.data.viewmodel.MainViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var balanceText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        balanceText = findViewById(R.id.balanceText)

        val db = AppDatabase.getDatabase(this)
        val factory = MainViewModelFactory(db.purchaseDao(), db.incomeDao())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Обновляем баланс
        viewModel.balance.observe(this) { balance ->
            balanceText.text = "Баланс: %.2f ₽".format(Locale.US, balance)
        }

        // Отображаем суммы по категориям
        viewModel.categoryTotals.observe(this) { list ->
            val categories = mapOf(
                "Продукты" to R.id.foodAmount,
                "Транспорт" to R.id.transportAmount,
                "Аптека" to R.id.pharmacyAmount,
                "Одежда" to R.id.clothesAmount,
                "Хобби" to R.id.entertainmentAmount,
                "ЖКХ" to R.id.rentAmount,
                "Другое" to R.id.otherAmount
            )

            // Сначала обнуляем всё
            categories.forEach { (_, viewId) ->
                findViewById<TextView>(viewId).text = "0 ₽"
            }

            // Обновляем имеющиеся
            list.forEach { total ->
                categories[total.category]?.let { id ->
                    findViewById<TextView>(id).text = "%.0f ₽".format(Locale.US, total.total)
                }
            }
        }

        // Кнопка дохода
        findViewById<MaterialButton>(R.id.incomeButton).setOnClickListener {
            val bottomSheet = AddIncomeBottomSheet { viewModel.updateBalance() }
            bottomSheet.show(supportFragmentManager, "AddIncomeBottomSheet")
        }

        viewModel.updateBalance()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateBalance()
    }
}

