package com.news2day.chamberly.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.firebase.await
import com.news2day.chamberly.firebase.awaitSuccess
import com.news2day.chamberly.model.dto.Signup
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseFirestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )?.await()

            val user = Signup().apply {
                this.name = name
                this.email = email.trim()
                this.user = result.user?.uid!!
            }
            firebaseFirestore.collection("Users").document(currentUser!!.uid).set(user)
                .awaitSuccess()

            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}