package org.macnigor.spendly.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.macnigor.spendly.data.database.IncomeDao
import org.macnigor.spendly.data.database.PurchaseDao

class MainViewModelFactory(
    private val purchaseDao: PurchaseDao,
    private val incomeDao: IncomeDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(purchaseDao, incomeDao) as T
    }
}