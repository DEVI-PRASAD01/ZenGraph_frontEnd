package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityForgotPasswordBinding
import com.simats.zengraph.network.ForgotPasswordRequest
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure we are on Step 1
        binding.viewFlipper.displayedChild = 0
        binding.stepIndicator.text = "Step 1 of 3"

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.btnNextStep1.setOnClickListener {
            val email = binding.emailInputStep1.text.toString().trim()
            if (validateEmail(email)) {
                sendResetCode(email)
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            true
        } else {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun sendResetCode(email: String) {
        binding.btnNextStep1.isEnabled = false
        Toast.makeText(this, "Sending code...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.forgotPassword(ForgotPasswordRequest(email))
                runOnUiThread {
                    binding.btnNextStep1.isEnabled = true
                    Toast.makeText(this@ForgotPasswordActivity, response.message, Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(this@ForgotPasswordActivity, OtpActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", email)
                    startActivity(intent)
                    overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.btnNextStep1.isEnabled = true
                    Toast.makeText(this@ForgotPasswordActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
