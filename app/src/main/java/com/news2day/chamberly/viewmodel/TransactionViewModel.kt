package com.news2day.chamberly.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.protobuf.Empty
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.model.dto.Transaction
import com.news2day.chamberly.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val repository: TransactionRepository) : ViewModel() {

    private val _addTransactionResult = MutableLiveData<Resource<Empty>?>(null)
    val addTransactionResult: LiveData<Resource<Empty>?> = _addTransactionResult

    private val _getAllTransactionsResult = MutableLiveData<ArrayList<Transaction?>?>()
    val getAllTransactionResult: LiveData<ArrayList<Transaction?>?> = _getAllTransactionsResult

    private val _getRecentTransactionResult = MutableLiveData<ArrayList<Transaction?>?>()
    val getRecentTransactionResult: LiveData<ArrayList<Transaction?>?> = _getRecentTransactionResult

    private val _userTotals = MutableLiveData<Resource<Map<String, Double>>>()
    val userTotals: LiveData<Resource<Map<String, Double>>> get() = _userTotals

    val currentUser:FirebaseUser?
        get() = repository.currentUser

    fun addTransaction(transaction:Transaction) = viewModelScope.launch{
        _addTransactionResult.value = Resource.Loading
        val result = repository.addTransaction(transaction)
        _addTransactionResult.value = result
    }

    fun getAllTransactions(userId:String) = viewModelScope.launch{
        //_getAllTransactionsResult.value = Resource.Loading
        when(val result: Resource<List<DocumentSnapshot>> = repository.getAllTransactions(userId)){
            is Resource.Success -> {
                val list = ArrayList<Transaction?>()
                for (i  in result.result){
                    val document = i.toObject(Transaction::class.java)
                    Log.d("Transactions", document.toString())
                    list.add(document)
                }
                _getAllTransactionsResult.value = list
            }

            Resource.Empty -> {

            }
            is Resource.Failure -> {

            }
            Resource.Loading -> {

            }
        }
        //val result = repository.getAllTransactions(userId)
//        _getAllTransactionsResult.value = result
    }

    fun getRecentTransactions(userId:String) = viewModelScope.launch{
        //_getAllTransactionsResult.value = Resource.Loading
        when(val result = repository.getRecentTransactions(userId)){
            is Resource.Success -> {
                val list = ArrayList<Transaction?>()
                for (i  in result.result){
                    val document = i.toObject(Transaction::class.java)
                    Log.d("Transactions", document.toString())
                    list.add(document)
                }
                _getRecentTransactionResult.value = list
            }

            Resource.Empty -> {

            }
            is Resource.Failure -> {

            }
            Resource.Loading -> {

            }
        }
        //val result = repository.getAllTransactions(userId)
//        _getAllTransactionsResult.value = result
    }

    fun fetchUserTotals(userId: String) {
        viewModelScope.launch {
            _userTotals.value = repository.getUserTotals(userId)
        }
    }

}