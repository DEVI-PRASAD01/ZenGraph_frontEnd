package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityForgotPasswordBinding
import com.simats.zengraph.network.ResetPasswordRequest
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val apiService = RetrofitClient.apiService
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("EXTRA_EMAIL")

        // Set to Step 3
        binding.viewFlipper.displayedChild = 2
        binding.stepIndicator.text = "Step 3 of 3"

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.btnResetStep3.setOnClickListener {
            val pass = binding.newPassInput.text.toString()
            val confirm = binding.confirmNewPassInput.text.toString()

            if (validatePasswords(pass, confirm)) {
                resetPassword(pass)
            }
        }
    }

    private fun validatePasswords(pass: String, confirm: String): Boolean {
        return when {
            pass.length < 6 -> {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                false
            }
            pass != confirm -> {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun resetPassword(newPassword: String) {
        val currentEmail = email ?: return
        binding.btnResetStep3.isEnabled = false
        Toast.makeText(this, "Updating password...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.resetPassword(ResetPasswordRequest(currentEmail, newPassword))
                runOnUiThread {
                    binding.btnResetStep3.isEnabled = true
                    Toast.makeText(this@ResetPasswordActivity, response.message, Toast.LENGTH_LONG).show()
                    
                    // Navigate to Login and clear back stack
                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.btnResetStep3.isEnabled = true
                    Toast.makeText(this@ResetPasswordActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
