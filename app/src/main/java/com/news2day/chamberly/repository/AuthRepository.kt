package com.news2day.chamberly.repository

import com.google.firebase.auth.FirebaseUser
import com.news2day.chamberly.data.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?

    suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser>

    suspend fun login(email: String, password: String): Resource<FirebaseUser>

    fun logout()
}