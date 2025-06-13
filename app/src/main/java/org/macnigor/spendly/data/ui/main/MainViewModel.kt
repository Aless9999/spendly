package org.macnigor.spendly.data.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.macnigor.spendly.data.database.dao.IncomeDao
import org.macnigor.spendly.data.database.dao.PurchaseDao
import org.macnigor.spendly.data.model.CategoryTotal
import org.macnigor.spendly.data.ui.common.Utilities

class MainViewModel(
    private val purchaseDao: PurchaseDao,
    private val incomeDao: IncomeDao
) : ViewModel() {

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance

    val categoryTotals: LiveData<List<CategoryTotal>> =
        purchaseDao.getCategoryTotals() // LiveData из DAO

    fun updateBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val util = Utilities(purchaseDao, incomeDao)
            val total = util.updateBalance()
            _balance.postValue(total)
        }
    }
}