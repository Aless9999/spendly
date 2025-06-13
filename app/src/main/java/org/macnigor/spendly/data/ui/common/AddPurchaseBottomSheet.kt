package org.macnigor.spendly.data.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.macnigor.spendly.R
import org.macnigor.spendly.data.database.db.AppDatabase
import org.macnigor.spendly.data.model.Purchase
import java.text.SimpleDateFormat
import java.util.*

class AddPurchaseBottomSheet : BottomSheetDialogFragment() {

    private var category: String? = null
    private var onSaveCallback: (() -> Unit)? = null


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
        val amountInput = view.findViewById<EditText>(R.id.purchaseAmountInput)
        val saveButton = view.findViewById<Button>(R.id.purchaseSaveButton)
        val nameVal = view.findViewById<EditText>(R.id.nameEditText).text.toString()
        saveButton.setOnClickListener {
            val dateVal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val amount = amountInput.text.toString().toDoubleOrNull()
            if (amount != null && category != null) {
                lifecycleScope.launch {
                    val purchase = Purchase(
                        amount = amount,
                        category = category!!,
                        name =nameVal,
                        date = dateVal
                    )

                    AppDatabase.getDatabase(requireContext()).purchaseDao().insert(purchase)

                    onSaveCallback?.invoke()
                    dismiss()
                }
            } else {
                Toast.makeText(context, "Введите сумму", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

