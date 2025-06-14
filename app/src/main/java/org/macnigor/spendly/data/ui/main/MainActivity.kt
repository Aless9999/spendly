package org.macnigor.spendly.data.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.db.AppDatabase
import org.macnigor.spendly.data.ui.common.AddIncomeBottomSheet
import org.macnigor.spendly.data.ui.common.AddPurchaseBottomSheet
import org.macnigor.spendly.data.ui.purchase.ReportActivity
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
                "Другое" to R.id.otherAmount,
                "Косметика" to R.id.cosmeticsAmount,
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

        // кнопка история
        findViewById<MaterialButton>(R.id.historyButton).setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        // Кнопки расходов
        val categories = mapOf(
            "Продукты" to R.id.foodButton,
            "Транспорт" to R.id.transportButton,
            "Аптека" to R.id.pharmacyButton,
            "Одежда" to R.id.clothesButton,
            "Хобби" to R.id.entertainmentButton,
            "ЖКХ" to R.id.rentButton,
            "Другое" to R.id.otherButton,
            "Косметика" to R.id.cosmeticsButton
        )

        categories.forEach { (categoryName, id) ->
            findViewById<MaterialButton>(id).setOnClickListener {
                val bottomSheet = AddPurchaseBottomSheet.newInstance(categoryName) {
                    viewModel.updateBalance()
                }
                bottomSheet.show(supportFragmentManager, "AddPurchaseBottomSheet")
            }
        }

        viewModel.updateBalance()

        // 🔽 Вызов анимации кнопок при запуске
        window.decorView.post {
            animateCategoryButtons()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateBalance()

        // 🔄 Повторная анимация при возвращении на экран
        animateCategoryButtons()
    }


    // 🔧 Анимация всплытия кнопок категорий
    private fun animateCategoryButtons() {
        val buttonIds = listOf(
            R.id.foodButton,
            R.id.transportButton,
            R.id.pharmacyButton,
            R.id.clothesButton,
            R.id.entertainmentButton,
            R.id.rentButton,
            R.id.otherButton,
            R.id.cosmeticsButton
        )

        buttonIds.forEachIndexed { index, id ->
            val button = findViewById<MaterialButton>(id)

            // Задаём стартовые параметры
            button.apply {
                scaleX = 0.8f
                scaleY = 0.8f
                alpha = 0f
                translationY = 50f
            }

            // Лог для отладки
            println("Animating button with id: $id")

            // Запускаем анимацию с задержкой, чтобы был эффект "всплытия" по очереди
            button.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(index * 80L)
                .start()
        }
    }



}
