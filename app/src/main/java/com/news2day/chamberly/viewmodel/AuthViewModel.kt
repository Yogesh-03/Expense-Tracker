package com.news2day.chamberly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor( private val repository: AuthRepository) : ViewModel() {

    private val _signupResult = MutableLiveData<Resource<FirebaseUser>?>(null)
    val signupResult: LiveData<Resource<FirebaseUser>?> = _signupResult

    private val _loginResult = MutableLiveData<Resource<FirebaseUser>?>(null)
    val loginResult: LiveData<Resource<FirebaseUser>?> = _loginResult

    val currentUser:FirebaseUser?
        get() = repository.currentUser

    init {
        if (repository.currentUser!=null){
            _loginResult.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun login(email:String, password:String) = viewModelScope.launch {
        _loginResult.value = Resource.Loading
        val result = repository.login(email, password)
        _loginResult.value = result
    }

    fun signUp(name:String, email:String, password:String) = viewModelScope.launch {
        _signupResult.value = Resource.Loading
        val result = repository.signUp(name, email, password)
        _signupResult.value = result
    }

    fun logout(){
        repository.logout()
        _loginResult.value = null
        _signupResult.value = null
    }
}