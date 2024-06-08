package com.news2day.chamberly.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.news2day.chamberly.adapter.TransactionsAdapter
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.databinding.FragmentDashboardBinding
import com.news2day.chamberly.viewmodel.AuthViewModel
import com.news2day.chamberly.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var binding:FragmentDashboardBinding
    private val viewModel by viewModels<TransactionViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())

        getRecentTransactions()

        viewModel.getRecentTransactionResult.observe(viewLifecycleOwner) {
            adapter = TransactionsAdapter(it)
            binding.rvRecentTransactions.adapter = adapter
            binding.pbDashboardFragment.visibility = View.GONE
        }

        // Fetch user available balance (income and expense)
        fetchUserTotals()

        // Observe user totals and update UI
        viewModel.userTotals.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val totals: Map<String, Double> = resource.result
                    val totalBalance = totals["Income"]!! + totals["Expense"]!!  //Expense is negative here
                    binding.tvAvailableBalance.text = totalBalance.toString()
                    binding.tvIncome.text = totals["Income"]!!.toString()
                    binding.tvExpense.text = totals["Expense"]!!.toString()
                }
                is Resource.Failure -> {
                    // Handle failure
                }
                else -> {
                    // Handle other cases
                }
            }
        }

        return binding.root
    }

    private fun getRecentTransactions() {
        viewModel.getRecentTransactions(authViewModel.currentUser!!.uid)
    }

    private fun fetchUserTotals() {
        viewModel.fetchUserTotals(authViewModel.currentUser!!.uid)
    }

    override fun onResume() {
        super.onResume()
        fetchUserTotals()
        getRecentTransactions()
    }
}