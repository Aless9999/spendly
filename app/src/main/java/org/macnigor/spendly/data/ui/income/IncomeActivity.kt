package org.macnigor.spendly.data.ui.income

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.IncomeDao

class IncomeActivity: AppCompatActivity() {
    private lateinit var incomeDao: IncomeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.income_activity)

    }

}
