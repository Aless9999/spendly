package org.macnigor.spendly.data.ui.purchase

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.dao.PurchaseDao
import org.macnigor.spendly.data.database.db.AppDatabase
import org.macnigor.spendly.data.model.Purchase
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var purchaseDao: PurchaseDao
    private lateinit var filterSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        pieChart = findViewById(R.id.pieChart)
        purchaseDao = AppDatabase.getDatabase(this).purchaseDao()

        // Spinner и адаптер

        filterSpinner = findViewById(R.id.filterSpinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.filter_options,
            R.layout.spinner_item_activity_report,

            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = adapter

        // Обработка выбора
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent.getItemAtPosition(position).toString()
                filterAndShowChart(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 🚀 По умолчанию "Сегодня"
        filterSpinner.setSelection(0)
    }


    private fun filterAndShowChart(range: String) {
        lifecycleScope.launch {
            val allPurchases = purchaseDao.getAllPurchases()
            val filtered = filterByDate(allPurchases, range)
            setupChart(filtered)
        }
    }

    private fun setupChart(purchases: List<Purchase>) {
        if (purchases.isEmpty()) {
            pieChart.clear()
            Toast.makeText(this, "Нет данных за выбранный период", Toast.LENGTH_SHORT).show()
            return
        }

        val pieEntries = purchases
            .groupBy { it.category }
            .mapNotNull {
                val sum = it.value.sumOf { p -> p.amount }
                if (sum > 0) PieEntry(sum.toFloat(), it.key) else null
            }

        val dataSet = PieDataSet(pieEntries, "Расходы по категориям").apply {
            colors = listOf(
                Color.parseColor("#F44336"), // красный

                // фиолетовый
                Color.parseColor("#673AB7"), // глубокий фиолетовый

                Color.parseColor("#2196F3"), // синий


                Color.parseColor("#4CAF50"), // зелёный


                Color.parseColor("#FFEB3B"), // жёлтый
                Color.parseColor("#FFC107"), // янтарный

                Color.parseColor("#FF5722"), // глубокий оранжевый
                Color.parseColor("#795548"), // коричневый
                Color.parseColor("#9E9E9E"), // серый
                Color.parseColor("#607D8B")  // сине-серый
            )
            setDrawValues(false)
            valueTextSize = 14f
            valueTextColor = Color.WHITE
        }

        val data = PieData(dataSet).apply {
            // setValueFormatter(PercentFormatter(pieChart))
        }

        pieChart.apply {
            this.data = data
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
            animateY(1000)

            // Применяем фон и цвета, в зависимости от ночного режима
            setBackgroundColor(Color.WHITE)
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)

            legend.apply {
                isEnabled = true
                textSize = 12f
                isWordWrapEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                form = Legend.LegendForm.CIRCLE
                xEntrySpace = 12f
                yEntrySpace = 8f
            }

            invalidate()
        }

    }


    private fun filterByDate(purchases: List<Purchase>, range: String): List<Purchase> {
        val now = Calendar.getInstance()
        val start = Calendar.getInstance()

        when (range) {
            "Сегодня" -> {
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)
            }

            "Вчера" -> {
                start.add(Calendar.DAY_OF_YEAR, -1)
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)

                val end = Calendar.getInstance().apply {
                    timeInMillis = start.timeInMillis
                    add(Calendar.DAY_OF_YEAR, 1)
                }

                return purchases.filter {
                    val time = parseDate(it.date)
                    time in start.timeInMillis until end.timeInMillis
                }
            }

            "Неделя" -> {
                start.add(Calendar.DAY_OF_YEAR, -7)
            }

            "Месяц" -> {
                start.add(Calendar.MONTH, -1)
            }

            "Год" -> {
                start.add(Calendar.YEAR, -1)
            }

            else -> return purchases
        }

        return purchases.filter {
            val time = parseDate(it.date)
            time >= start.timeInMillis
        }
    }

    private fun parseDate(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
