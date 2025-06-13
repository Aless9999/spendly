package org.macnigor.spendly.data.ui.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.db.AppDatabase
import org.macnigor.spendly.data.model.Purchase
import org.macnigor.spendly.data.ui.main.MainViewModel
import org.macnigor.spendly.data.viewmodel.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddPurchaseBottomSheet : BottomSheetDialogFragment() {

    private var category: String? = null
    private var onSaveCallback: (() -> Unit)? = null
    private lateinit var viewModel:MainViewModel



    companion object {
        fun newInstance(category: String, onSave: () -> Unit): AddPurchaseBottomSheet {
            val fragment = AddPurchaseBottomSheet()
            fragment.arguments = bundleOf("category" to category)
            fragment.onSaveCallback = onSave
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("category")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_purchase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val amountInput = view.findViewById<EditText>(R.id.purchaseAmountInput)
        val nameInput = view.findViewById<EditText>(R.id.nameEditText)
        val saveButton = view.findViewById<Button>(R.id.purchaseSaveButton)
        var currentBalance:Double = 0.00
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            currentBalance = balance
        }


        val hideKeyboardAndFocusButton: (View) -> Unit = { editText ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(editText.windowToken, 0)
            saveButton.requestFocus()
        }

        amountInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardAndFocusButton(amountInput)
                true // Остановить дальнейшую обработку
            } else {
                false
            }
        }

        nameInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboardAndFocusButton(nameInput)
                true // Остановить дальнейшую обработку
            } else {
                false
            }
        }

        saveButton.setOnClickListener {
            val dateVal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val amount = amountInput.text.toString().toDoubleOrNull()
            val nameVal = nameInput.text.toString()

            if (amount == null) {
                Toast.makeText(context, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount > currentBalance) {
                Toast.makeText(context, "Сумма превышает доступный баланс", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (category == null) {
                Toast.makeText(context, "Выберите категорию", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val purchase = Purchase(
                    amount = amount,
                    category = category!!,
                    name = nameVal,
                    date = dateVal
                )

                AppDatabase.getDatabase(requireContext()).purchaseDao().insert(purchase)

                onSaveCallback?.invoke()
                dismiss()
            }
        }
    }




}

