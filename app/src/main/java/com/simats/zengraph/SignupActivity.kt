package com.simats.zengraph

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.simats.zengraph.databinding.ActivitySignupBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SignupRequest
import com.simats.zengraph.repository.AuthRepository
import com.simats.zengraph.viewmodel.SignupState
import com.simats.zengraph.viewmodel.SignupViewModel
import com.simats.zengraph.viewmodel.SignupViewModelFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: SignupViewModel by viewModels {
        SignupViewModelFactory(AuthRepository(RetrofitClient.apiService))
    }

    // Country code state
    private var selectedCountryCode: String = "91"          // default: India
    private var selectedCountryLabel: String = "🇮🇳 +91"
    private var hasSelectedCountry: Boolean = false

    // Dynamically inserted feedback views
    private lateinit var tvRuleLength: TextView
    private lateinit var tvRuleUpper: TextView
    private lateinit var tvRuleLower: TextView
    private lateinit var tvRuleDigit: TextView
    private lateinit var tvRuleSpecial: TextView
    private lateinit var tvConfirmMatch: TextView

    // Available countries
    private val countries = listOf(
        Triple("🇮🇳 India",        "91",  "🇮🇳 +91"),
        Triple("🇺🇸 USA",           "1",   "🇺🇸 +1"),
        Triple("🇬🇧 UK",            "44",  "🇬🇧 +44")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        injectFeedbackViews()
        setupPhoneField()
        setupPasswordWatchers()
        setupConfirmPasswordWatcher()
        setupNameEmailWatchers()
        setupObservers()
        setupListeners()

        // Initial button state
        updateButtonState()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dynamic view injection (no XML changes)
    // ─────────────────────────────────────────────────────────────────────────

    private fun injectFeedbackViews() {
        // etPassword and etConfirmPassword both live in the same LinearLayout inside the CardView
        val parent = binding.etPassword.parent as android.view.ViewGroup

        // ------ Password rules (insert one by one after etPassword) ------
        // We insert them in order, each time using (currentIndex + 1) so they appear
        // in top-to-bottom order directly below etPassword.
        fun insertAfter(anchor: android.view.View, tv: TextView) {
            parent.addView(tv, parent.indexOfChild(anchor) + 1)
        }

        tvRuleLength  = createRuleView("Minimum 8 characters")
        tvRuleUpper   = createRuleView("At least one uppercase letter")
        tvRuleLower   = createRuleView("At least one lowercase letter")
        tvRuleDigit   = createRuleView("At least one number")
        tvRuleSpecial = createRuleView("At least one special character")

        // Insert starting from etPassword downward (each pushes next ones further down)
        insertAfter(binding.etPassword, tvRuleLength)
        insertAfter(tvRuleLength,  tvRuleUpper)
        insertAfter(tvRuleUpper,   tvRuleLower)
        insertAfter(tvRuleLower,   tvRuleDigit)
        insertAfter(tvRuleDigit,   tvRuleSpecial)

        // ------ Confirm password feedback (inserted right after etConfirmPassword) ------
        tvConfirmMatch = createRuleView("Confirm password")
        parent.addView(tvConfirmMatch, parent.indexOfChild(binding.etConfirmPassword) + 1)
    }

    private fun createRuleView(hint: String): TextView {
        return TextView(this).apply {
            text = "○  $hint"
            textSize = 12f
            setTextColor(Color.parseColor("#99AAAAAA"))
            setPadding(4, 4, 4, 0)
            // LayoutParams to match parent width
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Phone field setup
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupPhoneField() {
        // Restrict to digits only, max 10
        binding.etPhone.filters = arrayOf(
            DigitsKeyListener.getInstance("0123456789"),
            InputFilter.LengthFilter(10)
        )
        binding.etPhone.hint = "$selectedCountryLabel · 10-digit number"

        // On first tap: force country selection dialog before typing
        binding.etPhone.setOnClickListener {
            if (!hasSelectedCountry) {
                showCountryDialog()
            }
        }
    }

    private fun showCountryDialog() {
        val items = countries.map { "${it.first}  (${it.third})" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Country Code")
            .setCancelable(false)
            .setItems(items) { _, which ->
                val chosen = countries[which]
                selectedCountryCode  = chosen.second
                selectedCountryLabel = chosen.third
                hasSelectedCountry   = true
                // After selection, remove the forced dialog click listener
                binding.etPhone.setOnClickListener(null)
                binding.etPhone.hint = "$selectedCountryLabel · 10-digit number"
                binding.etPhone.requestFocus()
                updateButtonState()
            }
            .show()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TextWatchers
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupPasswordWatchers() {
        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            val pw = text.toString()
            setRule(tvRuleLength,  pw.length >= 8,              "Minimum 8 characters")
            setRule(tvRuleUpper,   pw.any { it.isUpperCase() }, "At least one uppercase letter")
            setRule(tvRuleLower,   pw.any { it.isLowerCase() }, "At least one lowercase letter")
            setRule(tvRuleDigit,   pw.any { it.isDigit() },     "At least one number")
            setRule(tvRuleSpecial, pw.any { !it.isLetterOrDigit() }, "At least one special character")
            // Also refresh confirm match
            refreshConfirmMatch()
            updateButtonState()
        }
    }

    private fun setupConfirmPasswordWatcher() {
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            refreshConfirmMatch()
            updateButtonState()
        }
    }

    private fun setupNameEmailWatchers() {
        binding.etName.doOnTextChanged  { _, _, _, _ -> updateButtonState() }
        binding.etEmail.doOnTextChanged { _, _, _, _ -> updateButtonState() }
        binding.etPhone.doOnTextChanged { _, _, _, _ -> updateButtonState() }
    }

    private fun refreshConfirmMatch() {
        val pw      = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()
        when {
            confirm.isEmpty() -> {
                tvConfirmMatch.text = "○  Confirm password"
                tvConfirmMatch.setTextColor(Color.parseColor("#99AAAAAA"))
            }
            pw == confirm -> {
                tvConfirmMatch.text = "✓  Passwords match"
                tvConfirmMatch.setTextColor(Color.parseColor("#FF4CAF50"))
            }
            else -> {
                tvConfirmMatch.text = "✗  Passwords do not match"
                tvConfirmMatch.setTextColor(Color.parseColor("#FFF44336"))
            }
        }
    }

    private fun setRule(tv: TextView, satisfied: Boolean, label: String) {
        if (satisfied) {
            tv.text = "✓  $label"
            tv.setTextColor(Color.parseColor("#FF4CAF50")) // green
        } else {
            tv.text = "○  $label"
            tv.setTextColor(Color.parseColor("#99AAAAAA")) // grey
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Button gating
    // ─────────────────────────────────────────────────────────────────────────

    private fun updateButtonState() {
        val name    = binding.etName.text.toString().trim()
        val email   = binding.etEmail.text.toString().trim()
        val phone   = binding.etPhone.text.toString()
        val pw      = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        val nameOk    = name.isNotEmpty()
        val emailOk   = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val phoneOk   = phone.length == 10 && selectedCountryCode.isNotEmpty()
        val pwOk      = pw.length >= 8
                     && pw.any { it.isUpperCase() }
                     && pw.any { it.isLowerCase() }
                     && pw.any { it.isDigit() }
                     && pw.any { !it.isLetterOrDigit() }
        val confirmOk = pw == confirm && confirm.isNotEmpty()

        binding.btnCreateAccount.isEnabled = nameOk && emailOk && phoneOk && pwOk && confirmOk
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Submit
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupListeners() {
        binding.btnCreateAccount.setOnClickListener {
            val fullPhone = "+$selectedCountryCode${binding.etPhone.text.toString().trim()}"
            val signupRequest = SignupRequest(
                name                = binding.etName.text.toString().trim(),
                email               = binding.etEmail.text.toString().trim(),
                phoneNumber         = fullPhone,
                password            = binding.etPassword.text.toString(),
                confirmPassword     = binding.etConfirmPassword.text.toString(),
                enableNotifications = binding.switchNotifications.isChecked
            )
            viewModel.signup(signupRequest)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Observers
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupObservers() {
        viewModel.signupState.observe(this) { state ->
            when (state) {
                is SignupState.Loading -> setLoading(true)
                is SignupState.Success -> {
                    setLoading(false)
                    Toast.makeText(this, state.response.message, Toast.LENGTH_LONG).show()
                    finish()
                }
                is SignupState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is SignupState.Idle -> setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnCreateAccount.isEnabled = !isLoading
    }
}
