package com.news2day.chamberly.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.protobuf.Empty
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.firebase.await
import com.news2day.chamberly.firebase.awaitSuccess
import com.news2day.chamberly.model.dto.Transaction
import javax.inject.Inject


class TransactionRepositoryImpl @Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseFirestore: FirebaseFirestore,
    private var firebaseRealtimeDatabase: DatabaseReference
) : TransactionRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun addTransaction(transaction: Transaction): Resource<Empty> {
        return try {
            //Add to Firebase Firestore
            firebaseFirestore.collection("Transactions").document()
                .set(transaction).awaitSuccess()

            //Add to realtime database

            val userTotalsRef = firebaseRealtimeDatabase.child("user_totals/${transaction.userId}")

            userTotalsRef.get().addOnSuccessListener { dataSnapshot ->
                val currentIncome = dataSnapshot.child("Income").getValue(Double::class.java) ?: 0.0
                val currentExpense =
                    dataSnapshot.child("Expense").getValue(Double::class.java) ?: 0.0

                val amount = Integer.valueOf(transaction.amount)

                val newIncome = if (amount >= 0) currentIncome + amount else currentIncome
                val newExpense = if (amount < 0) currentExpense + amount else currentExpense

                val updates = mapOf(
                    "Income" to newIncome,
                    "Expense" to newExpense
                )

                userTotalsRef.updateChildren(updates)
            }.await()

            Resource.Empty
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getAllTransactions(userId: String): Resource<List<DocumentSnapshot>> {
        return try {
            val result: QuerySnapshot = firebaseFirestore.collection("Transactions")
                .whereEqualTo("userId", currentUser!!.uid)
                .orderBy("date", Query.Direction.DESCENDING).get().awaitSuccess()
            Log.d("documents", result.documents.toString())
            Resource.Success(result.documents)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getRecentTransactions(userId: String): Resource<List<DocumentSnapshot>> {
        return try {
            val result = firebaseFirestore.collection("Transactions")
                .whereEqualTo("userId", userId)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .awaitSuccess()
            Resource.Success(result.documents)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserTotals(userId: String): Resource<Map<String, Double>> {
        return try {
            val userTotalsRef = firebaseRealtimeDatabase.child("user_totals/$userId")
            val dataSnapshot = userTotalsRef.get().await()
            val income = dataSnapshot.child("Income").getValue(Double::class.java) ?: 0.0
            val expense = dataSnapshot.child("Expense").getValue(Double::class.java) ?: 0.0
            Resource.Success(mapOf("Income" to income, "Expense" to expense))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}