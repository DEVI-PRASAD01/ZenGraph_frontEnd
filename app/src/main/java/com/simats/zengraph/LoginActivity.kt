package com.simats.zengraph

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityLoginBinding
import com.simats.zengraph.network.DashboardResponse
import com.simats.zengraph.network.LoginRequest
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.repository.AuthRepository
import com.simats.zengraph.viewmodel.LoginState
import com.simats.zengraph.viewmodel.LoginViewModel
import com.simats.zengraph.viewmodel.LoginViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Update hint programmatically — no XML change
        binding.etEmail.hint = "Email or phone number"

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            val input    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // If input contains "@" treat as email, otherwise treat as phone number
            val loginRequest = if (input.contains("@")) {
                LoginRequest(
                    email       = input,
                    phoneNumber = null,
                    password    = password
                )
            } else {
                // Strip leading "+" digits if user accidentally typed them
                val digits = input.trimStart('+').filter { it.isDigit() }
                // Default country code India (+91); extend with a selector if needed
                val fullPhone = "+91$digits"
                LoginRequest(
                    email       = null,
                    phoneNumber = fullPhone,
                    password    = password
                )
            }

            viewModel.login(loginRequest)
        }

        binding.forgotPasswordText.setOnClickListener {
            startAnimatedActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun startAnimatedActivity(intent: Intent, finishCurrent: Boolean = false) {
        startActivity(intent)
        overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
        if (finishCurrent) finish()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> setLoading(true)
                is LoginState.Success -> {
                    // Success is partially handled, but we need to fetch dashboard
                    val userId = state.response.userId
                    if (userId != null) {
                        val sharedPrefs = getSharedPreferences("ZenGraphPrefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit().putInt("user_id", userId).apply()
                        state.response.name?.let {
                            sharedPrefs.edit().putString("user_name", it).apply()
                        }
                        
                        // Fetch Dashboard after login success
                        fetchDashboardAndNavigate(userId, state.response.name ?: "User")
                    } else {
                        setLoading(false)
                        Toast.makeText(this, "Login successful, but User ID is missing", Toast.LENGTH_SHORT).show()
                    }
                }
                is LoginState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> setLoading(false)
            }
        }
    }

    private fun fetchDashboardAndNavigate(userId: Int, userName: String) {
        setLoading(true)
        lifecycleScope.launchWhenStarted {
            try {
                RetrofitClient.apiService.getDashboard(userId)
            } catch (_: Exception) { }

            setLoading(false)
            Toast.makeText(this@LoginActivity, "Welcome, $userName", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startAnimatedActivity(intent, true)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.isEnabled = !isLoading
    }
}
