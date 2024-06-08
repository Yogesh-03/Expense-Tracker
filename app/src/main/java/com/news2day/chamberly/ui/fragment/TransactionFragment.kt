package com.news2day.chamberly.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.news2day.chamberly.adapter.TransactionsAdapter
import com.news2day.chamberly.databinding.FragmentTransactionBinding
import com.news2day.chamberly.viewmodel.AuthViewModel
import com.news2day.chamberly.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionFragment : Fragment() {
    private lateinit var binding:FragmentTransactionBinding
    private val viewModel by viewModels<TransactionViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)

        binding.allTransactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        getAllTransactions()

        viewModel.getAllTransactionResult.observe(viewLifecycleOwner) {
            adapter = TransactionsAdapter(it)
            binding.allTransactionsRecyclerView.adapter = adapter
            binding.pbTransactionFragment.visibility = View.GONE
        }

        return binding.root
    }

    private fun getAllTransactions() {
        viewModel.getAllTransactions(authViewModel.currentUser!!.uid)
    }

    override fun onResume() {
        super.onResume()
        getAllTransactions()
    }
}