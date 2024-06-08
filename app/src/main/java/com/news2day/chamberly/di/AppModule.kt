package com.news2day.chamberly.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.news2day.chamberly.repository.AuthRepository
import com.news2day.chamberly.repository.AuthRepositoryImpl
import com.news2day.chamberly.repository.TransactionRepository
import com.news2day.chamberly.repository.TransactionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseRealtimeDatabase() : DatabaseReference = FirebaseDatabase.getInstance().reference
}