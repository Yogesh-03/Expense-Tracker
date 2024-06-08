package com.news2day.chamberly.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.protobuf.Empty
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.model.dto.Transaction

interface TransactionRepository {

    val currentUser: FirebaseUser?

    suspend fun addTransaction(transaction: Transaction) : Resource<Empty>

    suspend fun getAllTransactions(userId : String) : Resource<List<DocumentSnapshot>>
//    suspend fun updateTransaction(transaction: Transaction) : Resource<Nothing>

    suspend fun getRecentTransactions(userId: String) : Resource<List<DocumentSnapshot>>

    suspend fun getUserTotals(userId: String): Resource<Map<String, Double>>
}