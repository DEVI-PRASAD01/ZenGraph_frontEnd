package com.simats.zengraph

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simats.zengraph.databinding.ActivityLiveSessionBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SessionCompleteRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class LiveSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveSessionBinding
    private var sessionMood = ""
    private var sessionLevel = ""
    private var currentInstructions: BreathingInstructions? = null

    // ── Timer ──────────────────────────────────────────────────
    private var timer:              CountDownTimer? = null
    private var timeLeftInMillis:   Long = 0
    private var totalSessionMillis: Long = 0
    private var isPlaying           = true
    private var isProcessing        = false

    // ── Music ──────────────────────────────────────────────────
    private var mediaPlayer: MediaPlayer? = null
    private var isMusicOn    = true
    private var musicVolume  = 0.7f

    // ── Breathing Animation ────────────────────────────────────
    private val breathingHandler = Handler(Looper.getMainLooper())
    private var breathingRunnable: Runnable? = null
    private val INHALE_MS = 4000L
    private val HOLD_MS   = 4000L
    private val EXHALE_MS = 6000L
    private val REST_MS   = 2000L

    // ── TTS Voice ─────────────────────────────────────────────
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var currentGoal = ""      // session name e.g. "Ocean Breath Awareness"
    private var goalCategory = ""     // actual goal e.g. "Anxiety Relief"
    private var breathCycleCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val durationMinutes   = intent.getIntExtra("EXTRA_DURATION_MINUTES", 15)
        totalSessionMillis    = durationMinutes.toLong() * 60 * 1000
        timeLeftInMillis      = totalSessionMillis

        // currentGoal = session name shown in header
        currentGoal  = intent.getStringExtra("EXTRA_GOAL") ?: ""
        // goalCategory = the actual goal type passed from flow
        goalCategory = intent.getStringExtra("EXTRA_GOAL_CATEGORY") ?: ""
        sessionMood  = intent.getStringExtra("EXTRA_MOOD")  ?: "neutral"
        sessionLevel = intent.getStringExtra("EXTRA_LEVEL") ?: "beginner"

// Load the 108 instructions
        currentInstructions = MeditationInstructions.get(
            goal  = goalCategory.ifEmpty { getBreathingContext() },
            mood  = sessionMood,
            level = sessionLevel
        )

        binding.sessionTitle.text = currentGoal.ifEmpty { "Breathing Meditation" }

        binding.backButton.setOnClickListener { showPauseBottomSheet() }
        binding.btnPause.setOnClickListener   { showPauseBottomSheet() }
        binding.btnVolume.setOnClickListener     { toggleMusic() }
        binding.btnVolume.setOnLongClickListener { cycleVolume(); true }
        binding.btnSleepMode.setOnClickListener {
            Toast.makeText(this, "Focus Mode Active", Toast.LENGTH_SHORT).show()
        }

        startTimer()
        setupMusicPlayer()
        startGlowAnimation()
        startBreathingCycle()
        setupTTS()
    }

    // ════════════════════════════════════════════════════════════
    // BREATHING CONTEXT — checks BOTH session name AND goal category
    // ════════════════════════════════════════════════════════════

    private fun getBreathingContext(): String {
        // Combine both session name and goal category for matching
        val combined = "$currentGoal $goalCategory".lowercase()

        return when {
            // Sleep related
            combined.contains("sleep")   ||
                    combined.contains("midnight") ||
                    combined.contains("drift")   ||
                    combined.contains("cosmic")  ||
                    combined.contains("night")   ||
                    combined.contains("rest")    ||
                    combined.contains("worry")   -> "sleep"

            // Stress / Anxiety related
            combined.contains("stress")  ||
                    combined.contains("anxiety") ||
                    combined.contains("anxious") ||
                    combined.contains("storm")   ||
                    combined.contains("harbor")  ||
                    combined.contains("reduce")  ||
                    combined.contains("safe")    ||
                    combined.contains("healing") ||
                    combined.contains("gentle")  -> "stress"

            // Focus related
            combined.contains("focus")   ||
                    combined.contains("clarity") ||
                    combined.contains("peak")    ||
                    combined.contains("ignition")||
                    combined.contains("productiv")||
                    combined.contains("perform") ||
                    combined.contains("bridge")  -> "focus"

            // Happy / Joy related
            combined.contains("happy")   ||
                    combined.contains("happier") ||
                    combined.contains("joy")     ||
                    combined.contains("sunrise") ||
                    combined.contains("radiant") ||
                    combined.contains("amplif")  ||
                    combined.contains("positive")||
                    combined.contains("cheer")   -> "happy"

            // Mindful related
            combined.contains("mindful") ||
                    combined.contains("aware")   ||
                    combined.contains("present") ||
                    combined.contains("pure")    ||
                    combined.contains("witness") ||
                    combined.contains("pause")   -> "mindful"

            // Calm related
            combined.contains("calm")    ||
                    combined.contains("still")   ||
                    combined.contains("serene")  ||
                    combined.contains("peace")   ||
                    combined.contains("tranquil")||
                    combined.contains("zen")     ||
                    combined.contains("harmony") ||
                    combined.contains("ocean")   ||
                    combined.contains("water")   ||
                    combined.contains("deep")    ||
                    combined.contains("anchor")  -> "calm"

            // Gratitude / Loving kindness
            combined.contains("gratitude")||
                    combined.contains("loving")  ||
                    combined.contains("kindness")||
                    combined.contains("love")    ||
                    combined.contains("compassion") -> "happy"

            // Breathing specific
            combined.contains("breath")  ||
                    combined.contains("breathing")||
                    combined.contains("box")     ||
                    combined.contains("rhythmic")||
                    combined.contains("4-7-8")   -> "stress"

            // Body scan / grounding
            combined.contains("body")    ||
                    combined.contains("scan")    ||
                    combined.contains("ground")  ||
                    combined.contains("earth")   -> "mindful"

            else -> "calm" // default to calm instead of generic
        }
    }

    // ════════════════════════════════════════════════════════════
    // TTS VOICE GUIDANCE
    // ════════════════════════════════════════════════════════════

    private fun setupTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    return@TextToSpeech
                }
                tts?.setSpeechRate(0.78f)
                tts?.setPitch(0.88f)
                ttsReady = true
                breathingHandler.postDelayed({ speakWelcome() }, 1500)
            }
        }
    }

    private fun speakText(text: String, duckMusic: Boolean = true) {
        if (!ttsReady) return
        if (duckMusic) mediaPlayer?.setVolume(0.15f, 0.15f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "zen_tts")
        val estimatedDuration = (text.length * 60L) + 1500L
        breathingHandler.postDelayed({
            if (isMusicOn) mediaPlayer?.setVolume(musicVolume, musicVolume)
        }, estimatedDuration)
    }

    private fun speakWelcome() {
        speakText(currentInstructions?.welcome ?: "Welcome. Your meditation begins now.")
    }
    // ════════════════════════════════════════════════════════════
    // BREATHING CYCLE
    // ════════════════════════════════════════════════════════════

    private fun startBreathingCycle() {
        breathingRunnable?.let { breathingHandler.removeCallbacks(it) }
        breathCycleCount = 0
        doInhale()
    }

    private fun stopBreathingCycle() {
        breathingRunnable?.let { breathingHandler.removeCallbacks(it) }
        binding.breathingRing.animate().cancel()
        binding.tvBreathingPhase.text = ""
        binding.tvBreathingInstruction.text = "Session paused"
    }

    private fun doInhale() {
        binding.tvBreathingPhase.text = "BREATHE IN"
        binding.tvBreathingInstruction.text = currentInstructions?.inhaleInstruction
            ?: "Slowly inhale through your nose..."
        speakText(currentInstructions?.inhaleVoice ?: "Breathe in")

        binding.breathingRing.animate()
            .scaleX(1.4f).scaleY(1.4f).alpha(0.5f)
            .setDuration(INHALE_MS)
            .withEndAction {
                breathingRunnable = Runnable { doHold() }
                breathingHandler.post(breathingRunnable!!)
            }.start()
    }

    private fun doHold() {
        binding.tvBreathingPhase.text = "HOLD"
        binding.tvBreathingInstruction.text = currentInstructions?.holdInstruction
            ?: "Hold your breath gently..."
        speakText(currentInstructions?.holdVoice ?: "Hold")

        breathingRunnable = Runnable { doExhale() }
        breathingHandler.postDelayed(breathingRunnable!!, HOLD_MS)
    }

    private fun doExhale() {
        binding.tvBreathingPhase.text = "BREATHE OUT"
        binding.tvBreathingInstruction.text = currentInstructions?.exhaleInstruction
            ?: "Slowly exhale through your mouth..."
        speakText(currentInstructions?.exhaleVoice ?: "Breathe out")

        binding.breathingRing.animate()
            .scaleX(1.0f).scaleY(1.0f).alpha(0.25f)
            .setDuration(EXHALE_MS)
            .withEndAction {
                breathingRunnable = Runnable { doRest() }
                breathingHandler.post(breathingRunnable!!)
            }.start()
    }

    private fun doRest() {
        binding.tvBreathingPhase.text = "REST"
        binding.tvBreathingInstruction.text = currentInstructions?.restInstruction
            ?: "Relax and prepare for next breath..."

        breathCycleCount++
        if (breathCycleCount % 3 == 0) {
            speakText(currentInstructions?.motivational ?: "You are doing beautifully.", duckMusic = true)
        }

        breathingRunnable = Runnable { doInhale() }
        breathingHandler.postDelayed(breathingRunnable!!, REST_MS)
    }
    // ════════════════════════════════════════════════════════════
    // MUSIC PLAYER
    // ════════════════════════════════════════════════════════════

    private fun setupMusicPlayer() {
        try {
            val combined = "$currentGoal $goalCategory".lowercase()

            val musicFileName = currentInstructions?.musicFile ?: when {
                combined.contains("sleep")    ||
                        combined.contains("midnight") ||
                        combined.contains("cosmic")   ||
                        combined.contains("night")    -> "ocean_sleep"

                combined.contains("stress")   ||
                        combined.contains("anxiety")  ||
                        combined.contains("storm")    ||
                        combined.contains("harbor")   ||
                        combined.contains("safe")     ||
                        combined.contains("healing")  -> "rain_meditation"

                combined.contains("focus")    ||
                        combined.contains("clarity")  ||
                        combined.contains("peak")     ||
                        combined.contains("productiv")-> "morning_forest"

                combined.contains("happy")    ||
                        combined.contains("joy")      ||
                        combined.contains("sunrise")  ||
                        combined.contains("radiant")  ||
                        combined.contains("gratitude")||
                        combined.contains("loving")   -> "silver_river_keys"

                combined.contains("mindful")  ||
                        combined.contains("aware")    ||
                        combined.contains("pure")     ||
                        combined.contains("body")     ||
                        combined.contains("scan")     -> "ocean_waves"

                combined.contains("calm")     ||
                        combined.contains("still")    ||
                        combined.contains("serene")   ||
                        combined.contains("zen")      ||
                        combined.contains("harmony")  ||
                        combined.contains("ocean")    ||
                        combined.contains("water")    ||
                        combined.contains("peace")    ||
                        combined.contains("anchor")   ||
                        combined.contains("deep")     -> "deep_forest"

                else -> "rain_meditation"
            }

            var resId = resources.getIdentifier(musicFileName, "raw", packageName)
            if (resId == 0) resId = resources.getIdentifier("meditation_music", "raw", packageName)
            if (resId == 0) resId = resources.getIdentifier("meditation_audio", "raw", packageName)

            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId)
                mediaPlayer?.apply {
                    isLooping = true
                    setVolume(musicVolume, musicVolume)
                    start()
                }
                isMusicOn = true
                val displayName = musicFileName.replace("_", " ").replaceFirstChar { it.uppercase() }
                Toast.makeText(this, "♪ $displayName", Toast.LENGTH_SHORT).show()
            } else {
                isMusicOn = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isMusicOn = false
        }
    }

    private fun toggleMusic() {
        if (mediaPlayer == null) {
            Toast.makeText(this, "No music loaded", Toast.LENGTH_SHORT).show()
            return
        }
        isMusicOn = !isMusicOn
        if (isMusicOn) {
            mediaPlayer?.setVolume(musicVolume, musicVolume)
            if (mediaPlayer?.isPlaying == false) mediaPlayer?.start()
            Toast.makeText(this, "Music ON ♪", Toast.LENGTH_SHORT).show()
        } else {
            mediaPlayer?.setVolume(0f, 0f)
            Toast.makeText(this, "Music OFF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cycleVolume() {
        musicVolume = when {
            musicVolume < 0.4f -> 0.5f
            musicVolume < 0.6f -> 0.7f
            musicVolume < 0.9f -> 1.0f
            else               -> 0.3f
        }
        if (isMusicOn) mediaPlayer?.setVolume(musicVolume, musicVolume)
        val percent = (musicVolume * 100).toInt()
        Toast.makeText(this, "Volume: $percent%", Toast.LENGTH_SHORT).show()
    }

    // ════════════════════════════════════════════════════════════
    // TIMER
    // ════════════════════════════════════════════════════════════

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }
            override fun onFinish() {
                binding.timerText.text = "00:00"
                completeSession(isEarlyExit = false)
            }
        }.start()
        isPlaying = true
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        binding.timerText.text =
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    // ════════════════════════════════════════════════════════════
    // PAUSE BOTTOM SHEET
    // ════════════════════════════════════════════════════════════

    private fun showPauseBottomSheet() {
        timer?.cancel()
        isPlaying = false
        stopGlowAnimations()
        stopBreathingCycle()
        tts?.stop()
        mediaPlayer?.setVolume(0.2f, 0.2f)

        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_pause, null)

        view.findViewById<TextView>(R.id.btnResume).setOnClickListener {
            bottomSheet.dismiss()
            if (isMusicOn) mediaPlayer?.setVolume(musicVolume, musicVolume)
            if (isMusicOn && mediaPlayer?.isPlaying == false) mediaPlayer?.start()
            startTimer()
            startGlowAnimation()
            startBreathingCycle()
            breathingHandler.postDelayed({
                speakText("Welcome back. Let us continue.", duckMusic = true)
            }, 500)
        }

        view.findViewById<TextView>(R.id.btnEndSession).setOnClickListener {
            bottomSheet.dismiss()
            completeSession(isEarlyExit = true)
        }

        bottomSheet.setContentView(view)
        bottomSheet.setOnCancelListener {
            if (isMusicOn) mediaPlayer?.setVolume(musicVolume, musicVolume)
            startTimer()
            startGlowAnimation()
            startBreathingCycle()
        }
        bottomSheet.show()
    }

    // ════════════════════════════════════════════════════════════
    // GLOW ANIMATIONS
    // ════════════════════════════════════════════════════════════

    private fun startGlowAnimation() {
        val pulse = ScaleAnimation(
            1.0f, 1.15f, 1.0f, 1.15f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration    = 4000
            repeatMode  = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        val fade = AlphaAnimation(0.3f, 0.6f).apply {
            duration    = 4000
            repeatMode  = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.ringOuter.startAnimation(pulse)
        binding.timerGlow.startAnimation(fade)
    }

    private fun stopGlowAnimations() {
        binding.ringOuter.clearAnimation()
        binding.timerGlow.clearAnimation()
    }

    // ════════════════════════════════════════════════════════════
    // SESSION COMPLETION
    // ════════════════════════════════════════════════════════════

    private fun completeSession(isEarlyExit: Boolean = false) {
        if (isProcessing) return
        isProcessing = true

        timer?.cancel()
        stopGlowAnimations()
        stopBreathingCycle()
        tts?.stop()
        binding.btnPause.isEnabled = false

        if (!isEarlyExit && ttsReady) {
            speakText(
                "Well done. Your ${currentGoal.ifEmpty { "meditation" }} session is complete. " +
                        "Take a moment to appreciate yourself.",
                duckMusic = true
            )
        }

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        val dataManager = DataManager(this)
        val sessionId = intent.getIntExtra("EXTRA_SESSION_ID", -1)
            .takeIf { it != -1 }
            ?: SessionManager.currentSessionId
            ?: dataManager.sessionId

        val elapsedMillis  = totalSessionMillis - timeLeftInMillis
        val elapsedMinutes = (elapsedMillis / (1000 * 60)).toInt().coerceAtLeast(1)
        val plannedMinutes = (totalSessionMillis / (1000 * 60)).toInt()

        if (sessionId == -1) {
            navigateToCompletion(-1, elapsedMinutes, plannedMinutes, isEarlyExit)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.apiService.completeSession(
                    sessionId = sessionId,
                    request   = SessionCompleteRequest(
                        moodAfter      = "Relaxed",
                        actualDuration = elapsedMinutes
                    )
                )
                SessionManager.clearSession()
                dataManager.clearSession()
                runOnUiThread {
                    navigateToCompletion(sessionId, elapsedMinutes, plannedMinutes, isEarlyExit)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    navigateToCompletion(sessionId, elapsedMinutes, plannedMinutes, isEarlyExit)
                }
            }
        }
    }

    private fun navigateToCompletion(
        sessionId: Int, elapsed: Int,
        planned: Int, isEarlyExit: Boolean
    ) {
        val intent = Intent(this, SessionCompleteActivity::class.java).apply {
            putExtra("SESSION_ID",             sessionId)
            putExtra("COMPLETED_MINUTES",      elapsed)
            putExtra("EARLY_EXIT",             isEarlyExit)
            putExtra("EXTRA_DURATION_MINUTES", elapsed)
            putExtra("EXTRA_PLANNED_MINUTES",  planned)
            putExtra("IS_PARTIAL",             isEarlyExit)
        }
        startActivity(intent)
        finish()
    }

    // ════════════════════════════════════════════════════════════
    // LIFECYCLE
    // ════════════════════════════════════════════════════════════

    override fun onPause() {
        super.onPause()
        mediaPlayer?.setVolume(0.3f, 0.3f)
    }

    override fun onResume() {
        super.onResume()
        if (isMusicOn) mediaPlayer?.setVolume(musicVolume, musicVolume)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        stopBreathingCycle()
        tts?.stop()
        tts?.shutdown()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}