package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityForgotPasswordBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.VerifyOtpRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val apiService = RetrofitClient.apiService
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("EXTRA_EMAIL")

        // Set to Step 2
        binding.viewFlipper.displayedChild = 1
        binding.stepIndicator.text = "Step 2 of 3"

        setupOtpInputs()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.btnVerifyStep2.setOnClickListener {
            val otp = getOtpInput()
            if (otp.length == 4) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Please enter the 4-digit code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resendCode.setOnClickListener {
            email?.let { resendCode(it) }
        }
    }

    private fun setupOtpInputs() {
        val otpFields = listOf(binding.otpDigit1, binding.otpDigit2, binding.otpDigit3, binding.otpDigit4)
        for (i in 0 until otpFields.size - 1) {
            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) otpFields[i + 1].requestFocus()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun getOtpInput(): String {
        return listOf(binding.otpDigit1, binding.otpDigit2, binding.otpDigit3, binding.otpDigit4)
            .joinToString("") { it.text.toString() }
    }

    private fun verifyOtp(otp: String) {
        val currentEmail = email ?: return
        binding.btnVerifyStep2.isEnabled = false
        Toast.makeText(this, "Verifying...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.verifyOtp(VerifyOtpRequest(currentEmail, otp))
                runOnUiThread {
                    binding.btnVerifyStep2.isEnabled = true
                    Toast.makeText(this@OtpActivity, response.message, Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(this@OtpActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", currentEmail)
                    startActivity(intent)
                    overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.btnVerifyStep2.isEnabled = true
                    Toast.makeText(this@OtpActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun resendCode(email: String) {
        Toast.makeText(this, "Resending code to $email...", Toast.LENGTH_SHORT).show()
        // No explicit resend API provided in the request, so we mock or call forgot-password again
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Using forgotPassword for resending as it's the only one that sends OTP
                apiService.forgotPassword(com.simats.zengraph.network.ForgotPasswordRequest(email))
                runOnUiThread {
                    Toast.makeText(this@OtpActivity, "Code resent successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@OtpActivity, "Failed to resend: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
