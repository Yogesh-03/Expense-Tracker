package com.news2day.chamberly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.news2day.chamberly.data.Resource
import com.news2day.chamberly.databinding.ActivityMainBinding
import com.news2day.chamberly.ui.HomeActivity
import com.news2day.chamberly.ui.dialog.SignupDialog
import com.news2day.chamberly.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var signupDialog: SignupDialog
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = Intent(this, HomeActivity::class.java)

        viewModel.loginResult.observe(this) {
            when (it) {
                Resource.Empty -> {

                }

                is Resource.Failure -> {
                    Toast.makeText(this, "Enter valid credentials", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.visibility = View.VISIBLE
                }

                Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    startActivity(intent)
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.visibility = View.VISIBLE
                    finish()
                }

                null -> {

                }

            }
        }

        viewModel.signupResult.observe(this) {
            when (it) {
                Resource.Empty -> {

                }

                is Resource.Failure -> {
                    signupDialog.progressBar.visibility = View.GONE
                    signupDialog.dialogButtonSignup.visibility = View.VISIBLE
                    signupDialog.dismiss()
                }

                Resource.Loading -> {
                    signupDialog.progressBar.visibility = View.VISIBLE
                    signupDialog.dialogButtonSignup.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    startActivity(intent)
                    signupDialog.progressBar.visibility = View.GONE
                    signupDialog.dialogButtonSignup.visibility = View.VISIBLE
                    signupDialog.dismiss()
                    finish()
                }

                null -> {

                }

            }
        }

        binding.tvSignup.setOnClickListener {
            signupDialog = SignupDialog(this)
            signupDialog.onSignupButtonClicked = {name, email, password ->
                viewModel.signUp(name, email, password)
            }
            signupDialog.show()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.etLoginEmailText.text.isNullOrEmpty() || binding.etLoginPasswordText.text.isNullOrEmpty()){
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(binding.etLoginEmailText.text.toString(), binding.etLoginPasswordText.text.toString())
            }
        }
    }
}