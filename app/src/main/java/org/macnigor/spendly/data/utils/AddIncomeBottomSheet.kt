package org.macnigor.spendly.data.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.AppDatabase
import org.macnigor.spendly.data.database.IncomeDao
import org.macnigor.spendly.data.model.Income
import java.text.SimpleDateFormat
import java.util.*

class AddIncomeBottomSheet(
    private val onIncomeSaved: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var incomeDao: IncomeDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_add_income, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incomeDao = AppDatabase.getDatabase(requireContext()).incomeDao()

        val editAmount = view.findViewById<EditText>(R.id.editTextAmount)
        val editDescription = view.findViewById<EditText>(R.id.editTextDescription)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val amountStr = editAmount.text.toString()
            val description = editDescription.text.toString()

            if (amountStr.isBlank()) {
                Toast.makeText(context, "Введите сумму", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Некорректная сумма", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем в базу
            lifecycleScope.launch {
                addTransaction(amount,description)
                onIncomeSaved() // обновляем баланс на главном экране
                dismiss()
            }


        }
    }
    suspend fun addTransaction(amount: Double, category: String) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val income = Income(
            amount = amount,
            name = category,
            date = date
        )
        incomeDao.insert(income)
    }
}




