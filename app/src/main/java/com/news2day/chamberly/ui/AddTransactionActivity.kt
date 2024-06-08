package com.news2day.chamberly.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.news2day.chamberly.R
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.databinding.ActivityAddTransactionBinding
import com.news2day.chamberly.model.dto.Transaction
import com.news2day.chamberly.viewmodel.AuthViewModel
import com.news2day.chamberly.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel by viewModels<TransactionViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private var transactionType = -1 // -1 for expense, 1 for income
    private var selectedCategory = "none"
    private var selectedPaymentMethod = ""
    private  var selectedDate:String  = String()
    private  var selectedTime:String = String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val categoryItems = listOf("Shopping", "Rent", "Service", "Food", "Movies", "Fees", "Bills")

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryItems)
        binding.etCategoryText.setAdapter(adapter)
        binding.etCategoryText.setOnItemClickListener { parent, _, position, _ ->
            selectedCategory = parent.getItemAtPosition(position).toString()
        }

        val paymentOptions = listOf("Cash", "Card", "UPI")
        val paymentAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentOptions)
        binding.etPaymentMethodText.setAdapter(paymentAdapter)
        binding.etPaymentMethodText.setOnItemClickListener { parent, _, position, _ ->
            selectedPaymentMethod = parent.getItemAtPosition(position).toString()
        }

        binding.materialToolbarTransaction.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        binding.tvExpense.setOnClickListener {
            it.setBackgroundColor(resources.getColor(R.color.blue))
            binding.tvIncome.setBackgroundColor(resources.getColor(R.color.transparent))
            transactionType = -1
        }

        binding.tvIncome.setOnClickListener {
            it.setBackgroundColor(resources.getColor(R.color.blue))
            binding.tvExpense.setBackgroundColor(resources.getColor(R.color.transparent))
            transactionType = 1
        }

        binding.btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.btnOkTransaction.setOnClickListener {
            if (binding.etAmountText.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var amount = binding.etAmountText.text.toString()
            if (transactionType == -1){
                val amt = Integer.parseInt(amount)
                val newAmt = amt * -1
                amount = newAmt.toString()
                }

            viewModel.addTransaction(
                Transaction(
                    UUID.randomUUID().toString(),
                    viewModel.currentUser!!.uid,
                    selectedDate,
                    selectedTime,
                    amount,
                    selectedCategory,
                    selectedPaymentMethod,
                    binding.etRefText.text.toString(),
                    binding.etDescriptionText.text.toString()
                )
            )
        }

        binding.btnDeleteTransaction.setOnClickListener {
            Toast.makeText(this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.addTransactionResult.observe(this) {
            when (it) {
                Resource.Empty -> {
                    binding.pbTransaction.visibility = View.GONE
                    binding.llTransactionBtn.visibility = View.VISIBLE
                    viewModel.fetchUserTotals(authViewModel.currentUser!!.uid)
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                is Resource.Failure -> {
                    binding.pbTransaction.visibility = View.GONE
                    binding.llTransactionBtn.visibility = View.VISIBLE
                    Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show()
                }

                Resource.Loading -> {
                    binding.pbTransaction.visibility = View.VISIBLE
                    binding.llTransactionBtn.visibility = View.INVISIBLE
                }

                is Resource.Success -> {

                }

                null -> {

                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                 selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.tvSelectedDate.text = "Date: $selectedDate"
            }, year, month, day)

        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
             selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            binding.tvSelectedTime.text = "Time: $selectedTime"
        }, hour, minute, true)

        timePickerDialog.show()
    }
}