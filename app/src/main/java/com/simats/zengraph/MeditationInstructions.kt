package com.simats.zengraph

data class BreathingInstructions(
    val welcome: String,
    val inhaleInstruction: String,
    val inhaleVoice: String,
    val holdInstruction: String,
    val holdVoice: String,
    val exhaleInstruction: String,
    val exhaleVoice: String,
    val restInstruction: String,
    val motivational: String,
    val musicFile: String
)

object MeditationInstructions {

    // ── Keys: "goal_mood_level" ────────────────────────────────
    // Goals: stress, focus, sleep, happy, calm, mindful
    // Moods: anxious, sad, angry, happy, neutral, excited
    // Levels: beginner, intermediate, advanced

    private val instructions: Map<String, BreathingInstructions> = mapOf(

        // ════════════════════════════════════════════════════════
        // STRESS — ANXIOUS
        // ════════════════════════════════════════════════════════

        "stress_anxious_beginner" to BreathingInstructions(
            welcome = "Welcome. You are safe here. Let's take this one breath at a time. There is nothing you need to do right now except breathe.",
            inhaleInstruction = "Gently breathe in through your nose, count to 4...",
            inhaleVoice = "Breathe in gently... one... two... three... four",
            holdInstruction = "Hold softly. You are safe. Nothing can harm you here.",
            holdVoice = "Hold... you are safe here",
            exhaleInstruction = "Slowly release all the worry as you breathe out...",
            exhaleVoice = "Breathe out all your worries... slowly",
            restInstruction = "Rest. Feel how much lighter you already are.",
            motivational = "With every breath, your anxiety is releasing. You are doing wonderfully.",
            musicFile = "rain_meditation"
        ),

        "stress_anxious_intermediate" to BreathingInstructions(
            welcome = "Welcome back. You know this feeling — and you know you can move through it. Let your breath be your anchor right now.",
            inhaleInstruction = "Breathe in through your nose for 4 counts, expanding your belly...",
            inhaleVoice = "Inhale deeply into your belly... let it expand",
            holdInstruction = "Hold for 4 counts. Notice the stillness between the breaths.",
            holdVoice = "Hold... notice the stillness",
            exhaleInstruction = "Exhale for 6 counts. Let your nervous system calm down.",
            exhaleVoice = "Long slow exhale... your nervous system is calming",
            restInstruction = "Rest and observe. Your body is returning to balance.",
            motivational = "Your nervous system is recalibrating with each breath. Trust the process.",
            musicFile = "rain_meditation"
        ),

        "stress_anxious_advanced" to BreathingInstructions(
            welcome = "Welcome. You have the tools. Use this session to practice non-attachment to anxious thoughts. Observe them like clouds passing.",
            inhaleInstruction = "Breathe in with complete diaphragmatic engagement...",
            inhaleVoice = "Full diaphragmatic inhale... feel the expansion",
            holdInstruction = "Hold and practice non-reactive awareness of any tension.",
            holdVoice = "Hold... observe without reaction",
            exhaleInstruction = "Exhale completely, releasing the last remnants of tension.",
            exhaleVoice = "Complete exhale... release every last bit of tension",
            restInstruction = "Rest in equanimity. You are the observer, not the anxiety.",
            motivational = "You are not your thoughts. You are the awareness watching them pass.",
            musicFile = "rain_meditation"
        ),

        // ── STRESS — SAD ──────────────────────────────────────

        "stress_sad_beginner" to BreathingInstructions(
            welcome = "Welcome. It's okay to feel sad. You don't need to fix anything right now. Just breathe and let yourself be held by this moment.",
            inhaleInstruction = "Breathe in warmth and self-compassion...",
            inhaleVoice = "Breathe in warmth and kindness for yourself",
            holdInstruction = "Hold and send yourself a gentle message of love.",
            holdVoice = "Hold... and send yourself love",
            exhaleInstruction = "Breathe out the heaviness. You don't need to carry it all.",
            exhaleVoice = "Breathe out the heaviness... you don't have to carry it",
            restInstruction = "Rest. You are doing enough. You are enough.",
            motivational = "You are allowed to feel this. And you are allowed to heal.",
            musicFile = "silver_river_keys"
        ),

        "stress_sad_intermediate" to BreathingInstructions(
            welcome = "Welcome. Sadness is a signal, not a sentence. Let's use your breath to process this emotion with compassion.",
            inhaleInstruction = "Inhale and imagine breathing in golden healing light...",
            inhaleVoice = "Inhale golden healing light into your heart",
            holdInstruction = "Hold this healing light in your heart center.",
            holdVoice = "Hold the light in your heart",
            exhaleInstruction = "Exhale grey mist — all the sadness leaving your body.",
            exhaleVoice = "Exhale the grey mist of sadness",
            restInstruction = "Rest. Notice the warmth growing in your chest.",
            motivational = "With each breath, you are transmuting sadness into strength.",
            musicFile = "silver_river_keys"
        ),

        "stress_sad_advanced" to BreathingInstructions(
            welcome = "Welcome. As a practitioner, you understand that sadness is part of the full spectrum of being alive. Let's be with it fully.",
            inhaleInstruction = "Breathe in and welcome this emotion without resistance...",
            inhaleVoice = "Breathe in and welcome this feeling fully",
            holdInstruction = "Hold and sit with the emotion. Do not push it away.",
            holdVoice = "Hold... sit with the emotion, don't resist it",
            exhaleInstruction = "Exhale slowly, allowing the emotion to complete its cycle.",
            exhaleVoice = "Exhale and allow the emotion to complete",
            restInstruction = "Rest in the spaciousness that exists beyond the sadness.",
            motivational = "You are big enough to hold this feeling. And bigger than it.",
            musicFile = "silver_river_keys"
        ),

        // ── STRESS — ANGRY ────────────────────────────────────

        "stress_angry_beginner" to BreathingInstructions(
            welcome = "Welcome. Anger is energy. Let's transform it. Start by just noticing your breath without changing it.",
            inhaleInstruction = "Breathe in slowly. Let the breath cool the heat inside...",
            inhaleVoice = "Breathe in cooling air... feel it cool the heat",
            holdInstruction = "Hold and pause before reacting. This space is power.",
            holdVoice = "Hold... this pause is your power",
            exhaleInstruction = "Breathe out fire and frustration. Let it go completely.",
            exhaleVoice = "Breathe out the fire and frustration",
            restInstruction = "Rest. Feel the temperature inside you dropping.",
            motivational = "You are choosing calm over reaction. That is true strength.",
            musicFile = "rain_meditation"
        ),

        "stress_angry_intermediate" to BreathingInstructions(
            welcome = "Welcome. You already know that anger clouds clarity. Let's use the 4-7-8 technique to reset your nervous system.",
            inhaleInstruction = "Inhale for 4 counts, drawing in calm and perspective...",
            inhaleVoice = "Inhale calm and perspective... four counts",
            holdInstruction = "Hold for 7 counts. Let the calm penetrate every cell.",
            holdVoice = "Hold for seven... let calm penetrate every cell",
            exhaleInstruction = "Exhale for 8 counts. Release all resistance and anger.",
            exhaleVoice = "Long exhale for eight... release all resistance",
            restInstruction = "Rest. Notice how the anger has lost its charge.",
            motivational = "Your response to this situation is becoming clearer and wiser.",
            musicFile = "rain_meditation"
        ),

        "stress_angry_advanced" to BreathingInstructions(
            welcome = "Welcome. At this level, you understand anger as unmet needs. Let's breathe into what's underneath the anger.",
            inhaleInstruction = "Breathe in and ask — what need is this anger protecting?",
            inhaleVoice = "Breathe in... what need is beneath this anger?",
            holdInstruction = "Hold and listen to what your anger is really telling you.",
            holdVoice = "Hold... listen to what the anger is protecting",
            exhaleInstruction = "Exhale the anger, keep the wisdom it carries.",
            exhaleVoice = "Exhale the anger, keep its wisdom",
            restInstruction = "Rest. You are transforming anger into understanding.",
            motivational = "Behind every anger is a need. You are finding yours.",
            musicFile = "rain_meditation"
        ),

        // ── STRESS — HAPPY ────────────────────────────────────

        "stress_happy_beginner" to BreathingInstructions(
            welcome = "Welcome. What a gift to begin this practice from a place of happiness. Let's deepen this feeling through breath.",
            inhaleInstruction = "Breathe in and amplify the good feeling already inside you...",
            inhaleVoice = "Breathe in and amplify this good feeling",
            holdInstruction = "Hold and let this happiness sink into every part of you.",
            holdVoice = "Hold... let happiness sink in deeper",
            exhaleInstruction = "Breathe out any remaining stress. You deserve to feel this good.",
            exhaleVoice = "Breathe out any remaining stress",
            restInstruction = "Rest in this beautiful state. Let it be your natural home.",
            motivational = "This happy, calm state is your true nature. Welcome home.",
            musicFile = "silver_river_keys"
        ),

        "stress_happy_intermediate" to BreathingInstructions(
            welcome = "Welcome. You are starting from a great place. Let's use this positive energy to dissolve any background stress completely.",
            inhaleInstruction = "Inhale joy and let it wash away any residual tension...",
            inhaleVoice = "Inhale joy and let it dissolve all tension",
            holdInstruction = "Hold and let the joy neutralize any hidden stress.",
            holdVoice = "Hold... joy is neutralizing all stress",
            exhaleInstruction = "Exhale the last traces of stress from your system.",
            exhaleVoice = "Exhale the last traces of stress",
            restInstruction = "Rest. You are now operating at your highest frequency.",
            motivational = "Your positive state is a superpower. Breathe it into permanence.",
            musicFile = "silver_river_keys"
        ),

        "stress_happy_advanced" to BreathingInstructions(
            welcome = "Welcome. From this joyful state, let's practice gratitude breathing to fortify your resilience against future stress.",
            inhaleInstruction = "Inhale gratitude for this moment exactly as it is...",
            inhaleVoice = "Inhale gratitude for this perfect moment",
            holdInstruction = "Hold and send this grateful energy outward to others.",
            holdVoice = "Hold... send this grateful energy outward",
            exhaleInstruction = "Exhale blessings and goodwill to all beings.",
            exhaleVoice = "Exhale blessings to all beings",
            restInstruction = "Rest in the interconnectedness of all life.",
            motivational = "Your happiness, shared through breath, elevates the world around you.",
            musicFile = "silver_river_keys"
        ),

        // ── STRESS — NEUTRAL ──────────────────────────────────

        "stress_neutral_beginner" to BreathingInstructions(
            welcome = "Welcome. You are in a perfect neutral state — like a blank canvas. Let's use this session to release any hidden tension you might not even notice.",
            inhaleInstruction = "Breathe in awareness and gentle attention to your body...",
            inhaleVoice = "Breathe in gentle awareness",
            holdInstruction = "Hold and scan your body for any tension hiding beneath the surface.",
            holdVoice = "Hold... scan for hidden tension",
            exhaleInstruction = "Breathe out whatever you found. You don't need it.",
            exhaleVoice = "Breathe out whatever tension you found",
            restInstruction = "Rest. Notice your body becoming progressively lighter.",
            motivational = "You are doing preventive care for your nervous system. Well done.",
            musicFile = "deep_forest"
        ),

        "stress_neutral_intermediate" to BreathingInstructions(
            welcome = "Welcome. Your neutral state is an advantage. From here, we can go deep without fighting through emotional noise.",
            inhaleInstruction = "Breathe in deeply, filling all three chambers of your lungs...",
            inhaleVoice = "Fill all three chambers... belly, chest, collarbones",
            holdInstruction = "Hold and feel the full expansion of your breath.",
            holdVoice = "Hold... feel the full expansion",
            exhaleInstruction = "Exhale completely from collarbones down to the belly.",
            exhaleVoice = "Exhale from top to bottom... completely",
            restInstruction = "Rest. This is optimal breathing. This is your natural state.",
            motivational = "Your body knows exactly how to heal itself. Trust it.",
            musicFile = "deep_forest"
        ),

        "stress_neutral_advanced" to BreathingInstructions(
            welcome = "Welcome. Neutral mind is the most powerful state for deep practice. Let's use this session for pranayama at its most refined level.",
            inhaleInstruction = "Controlled ujjayi inhale — slight throat constriction...",
            inhaleVoice = "Ujjayi inhale... slight throat constriction, ocean sound",
            holdInstruction = "Kumbhaka — held breath. Mula bandha engaged.",
            holdVoice = "Kumbhaka hold... engage mula bandha",
            exhaleInstruction = "Controlled ujjayi exhale — twice as long as the inhale.",
            exhaleVoice = "Slow ujjayi exhale... twice the length of inhale",
            restInstruction = "Bahya kumbhaka — brief empty hold. Pure awareness.",
            motivational = "You are practicing the ancient art of pranayama. Breathe with mastery.",
            musicFile = "deep_forest"
        ),

        // ── STRESS — EXCITED ──────────────────────────────────

        "stress_excited_beginner" to BreathingInstructions(
            welcome = "Welcome. Your excitement is wonderful energy! Let's channel it through your breath to create a beautiful balance of energy and calm.",
            inhaleInstruction = "Breathe in and feel the exciting energy becoming organized...",
            inhaleVoice = "Breathe in and let your energy organize itself",
            holdInstruction = "Hold and let the excitement settle into groundedness.",
            holdVoice = "Hold... let excitement settle into groundedness",
            exhaleInstruction = "Breathe out any scattered energy. Feel yourself centering.",
            exhaleVoice = "Breathe out scattered energy... find your center",
            restInstruction = "Rest. Feel excitement and calm coexisting perfectly.",
            motivational = "You are turning raw excitement into focused, powerful energy.",
            musicFile = "morning_forest"
        ),

        "stress_excited_intermediate" to BreathingInstructions(
            welcome = "Welcome. High energy, meet deep breath. Let's use rhythmic breathing to harness your excitement into sustained, productive calm.",
            inhaleInstruction = "Rhythmic inhale — match your breath to a steady internal beat...",
            inhaleVoice = "Rhythmic inhale... find your steady beat",
            holdInstruction = "Hold and feel the rhythm organizing your energy.",
            holdVoice = "Hold the rhythm... feel your energy organize",
            exhaleInstruction = "Rhythmic exhale — same count as the inhale. Even and steady.",
            exhaleVoice = "Even rhythmic exhale... steady and smooth",
            restInstruction = "Rest. Your energy is now coherent and focused.",
            motivational = "Coherent energy is ten times more powerful than scattered excitement.",
            musicFile = "morning_forest"
        ),

        "stress_excited_advanced" to BreathingInstructions(
            welcome = "Welcome. At the advanced level, we work with excited energy as fuel for deeper states. Let's transmute this excitement into profound stillness.",
            inhaleInstruction = "Inhale and draw the excited energy upward through your spine...",
            inhaleVoice = "Draw excited energy up through your spine",
            holdInstruction = "Hold and let the energy gather at the crown of your head.",
            holdVoice = "Hold... energy gathering at the crown",
            exhaleInstruction = "Exhale and release the energy as pure white light outward.",
            exhaleVoice = "Exhale pure white light outward from the crown",
            restInstruction = "Rest. The excitement has transformed into luminous awareness.",
            motivational = "You have alchemized excitement into transcendent awareness.",
            musicFile = "morning_forest"
        ),

        // ════════════════════════════════════════════════════════
        // FOCUS — ALL MOODS
        // ════════════════════════════════════════════════════════

        "focus_anxious_beginner" to BreathingInstructions(
            welcome = "Welcome. I know focusing feels hard when you're anxious. Let's start small — just one breath at a time. Focus will come naturally.",
            inhaleInstruction = "Breathe in and count slowly — this counting IS your focus...",
            inhaleVoice = "Breathe in and count... this is focus practice",
            holdInstruction = "Hold and keep your attention only on the sensation of holding.",
            holdVoice = "Hold... keep attention only here",
            exhaleInstruction = "Breathe out and count the exhale. Numbers anchor a wandering mind.",
            exhaleVoice = "Breathe out and count... numbers anchor the mind",
            restInstruction = "Rest. Each cycle is building your focus muscle.",
            motivational = "Focus is a muscle. You are building it right now, breath by breath.",
            musicFile = "morning_forest"
        ),

        "focus_anxious_intermediate" to BreathingInstructions(
            welcome = "Welcome. Anxiety and focus are incompatible states — but breath is the bridge between them. Let's cross that bridge.",
            inhaleInstruction = "Inhale to expand your peripheral vision — the anxiety shrinks...",
            inhaleVoice = "Inhale and expand your peripheral awareness",
            holdInstruction = "Hold and notice anxiety losing its grip as focus grows.",
            holdVoice = "Hold... feel focus replacing anxiety",
            exhaleInstruction = "Exhale anxiety, inhale was for focus — now release the anxiety.",
            exhaleVoice = "Exhale the last of the anxiety",
            restInstruction = "Rest. You are shifting from scattered to single-pointed.",
            motivational = "Single-pointed focus is your superpower emerging right now.",
            musicFile = "morning_forest"
        ),

        "focus_anxious_advanced" to BreathingInstructions(
            welcome = "Welcome. At your level, you know anxiety is just misdirected attention. Let's redirect it into laser-sharp focus using pranayama.",
            inhaleInstruction = "Nadi Shodhana — alternate nostril breathing. Right nostril inhale...",
            inhaleVoice = "Right nostril inhale... close the left",
            holdInstruction = "Kumbhaka — both nostrils closed. Pure stillness.",
            holdVoice = "Both nostrils closed... pure stillness",
            exhaleInstruction = "Left nostril exhale — balancing both hemispheres.",
            exhaleVoice = "Left nostril exhale... balancing hemispheres",
            restInstruction = "Rest. Both brain hemispheres are now synchronized.",
            motivational = "Your brain is in hemispheric synchrony — the state of genius.",
            musicFile = "morning_forest"
        ),

        "focus_sad_beginner" to BreathingInstructions(
            welcome = "Welcome. Sadness can make it hard to focus. That's completely normal. Let's gently invite clarity through simple, kind breathing.",
            inhaleInstruction = "Breathe in light — imagine it brightening your mind gently...",
            inhaleVoice = "Breathe in light... brighten your mind gently",
            holdInstruction = "Hold the light there. Let it illuminate one clear thought.",
            holdVoice = "Hold the light... one clear thought",
            exhaleInstruction = "Breathe out the fog. The sadness doesn't have to block your clarity.",
            exhaleVoice = "Breathe out the mental fog",
            restInstruction = "Rest. Your mind is clearing like morning mist lifting.",
            motivational = "Clarity is returning. You are capable of brilliant focus.",
            musicFile = "morning_forest"
        ),

        "focus_sad_intermediate" to BreathingInstructions(
            welcome = "Welcome. Emotional pain and mental clarity can coexist. Let's compartmentalize through breath — honor the feeling, then set it aside for now.",
            inhaleInstruction = "Inhale and create a mental container for your sadness...",
            inhaleVoice = "Inhale and create a container for the sadness",
            holdInstruction = "Hold and place the sadness safely in the container.",
            holdVoice = "Hold... place the sadness safely aside for now",
            exhaleInstruction = "Exhale and step into your clarity zone.",
            exhaleVoice = "Exhale and step into your clarity zone",
            restInstruction = "Rest in the clear space you've created.",
            motivational = "You can feel deeply AND think clearly. Both exist in you.",
            musicFile = "morning_forest"
        ),

        "focus_sad_advanced" to BreathingInstructions(
            welcome = "Welcome. Advanced practitioners know that emotion, when metabolized through breath, becomes fuel for extraordinary focus. Let's do that.",
            inhaleInstruction = "Inhale and transmute the sadness into fuel for clarity...",
            inhaleVoice = "Inhale... transmute sadness into fuel for focus",
            holdInstruction = "Hold and feel sadness converting into motivation energy.",
            holdVoice = "Hold... sadness converting to motivation",
            exhaleInstruction = "Exhale the residue. Keep only the pure focused energy.",
            exhaleVoice = "Exhale the residue, keep only focused energy",
            restInstruction = "Rest. Your sadness has become your sharpest tool.",
            motivational = "The deepest focus often comes from those who have felt the deepest.",
            musicFile = "morning_forest"
        ),

        "focus_angry_beginner" to BreathingInstructions(
            welcome = "Welcome. Anger has incredible energy — it just needs direction. Let's breathe that energy into razor-sharp focus right now.",
            inhaleInstruction = "Breathe in and channel the anger's fire into your focus...",
            inhaleVoice = "Breathe in... channel the fire into focus",
            holdInstruction = "Hold and feel the anger transforming into determination.",
            holdVoice = "Hold... anger becoming determination",
            exhaleInstruction = "Breathe out the reactive part. Keep the drive.",
            exhaleVoice = "Breathe out the reaction, keep the drive",
            restInstruction = "Rest. Your anger is now laser-focused energy.",
            motivational = "Anger directed inward becomes destruction. Directed forward — it becomes greatness.",
            musicFile = "morning_forest"
        ),

        "focus_angry_intermediate" to BreathingInstructions(
            welcome = "Welcome. You know anger clouds judgment. Let's use box breathing to reset your prefrontal cortex — the seat of clear thinking.",
            inhaleInstruction = "Box breathing — inhale 4, creating mental clarity...",
            inhaleVoice = "Box inhale for four... building clarity",
            holdInstruction = "Hold 4 — prefrontal cortex coming online.",
            holdVoice = "Hold four... rational mind coming online",
            exhaleInstruction = "Exhale 4 — releasing the limbic system's grip.",
            exhaleVoice = "Exhale four... releasing the emotional hijack",
            restInstruction = "Hold empty 4. Pure clear space. Genius space.",
            motivational = "Your rational mind is back in control. Think with power and clarity.",
            musicFile = "morning_forest"
        ),

        "focus_angry_advanced" to BreathingInstructions(
            welcome = "Welcome. Warrior breath — for those who channel anger into mastery. Today we forge frustration into flawless focus.",
            inhaleInstruction = "Kapalabhati — rapid forceful exhales, passive inhales...",
            inhaleVoice = "Kapalabhati... rapid pumping, clear the mental fog",
            holdInstruction = "Retention after exhale. Stillness. Pure awareness.",
            holdVoice = "Bahya kumbhaka... complete stillness",
            exhaleInstruction = "Long slow exhale — releasing the last of the anger charge.",
            exhaleVoice = "Long exhale... anger fully released",
            restInstruction = "Rest. You are now a focused, clear, and powerful being.",
            motivational = "You have transformed raw anger into the fuel of champions.",
            musicFile = "morning_forest"
        ),

        "focus_happy_beginner" to BreathingInstructions(
            welcome = "Welcome. Starting focus practice from happiness is a gift. Your mind is already open and receptive. Let's channel that into deep work.",
            inhaleInstruction = "Breathe in and set a clear intention for what you want to focus on...",
            inhaleVoice = "Breathe in and set your focus intention",
            holdInstruction = "Hold and anchor that intention in your mind.",
            holdVoice = "Hold... anchor your intention",
            exhaleInstruction = "Breathe out everything except that one clear focus.",
            exhaleVoice = "Breathe out everything else... only your focus remains",
            restInstruction = "Rest in single-pointed clarity.",
            motivational = "Happy focus is the most productive state known to neuroscience.",
            musicFile = "morning_forest"
        ),

        "focus_happy_intermediate" to BreathingInstructions(
            welcome = "Welcome. Happiness enhances cognitive function. Let's leverage your positive state for extraordinary mental clarity and sustained attention.",
            inhaleInstruction = "Inhale and let happiness expand your cognitive bandwidth...",
            inhaleVoice = "Inhale... happiness expanding your mental bandwidth",
            holdInstruction = "Hold and feel your working memory at peak capacity.",
            holdVoice = "Hold... peak cognitive performance",
            exhaleInstruction = "Exhale and lock in this peak performance state.",
            exhaleVoice = "Exhale and lock in peak performance",
            restInstruction = "Rest. You are operating at optimal cognitive capacity.",
            motivational = "You are in flow state. The work you do from here will be exceptional.",
            musicFile = "morning_forest"
        ),

        "focus_happy_advanced" to BreathingInstructions(
            welcome = "Welcome. The combination of happiness and advanced focus practice produces states that neuroscientists call transcendent creativity. Let's go there.",
            inhaleInstruction = "Inhale and access the superconscious creativity within you...",
            inhaleVoice = "Inhale into superconscious creative awareness",
            holdInstruction = "Hold at the peak — this is where insight and genius live.",
            holdVoice = "Hold at the peak... this is where genius lives",
            exhaleInstruction = "Exhale and allow the insights to crystallize.",
            exhaleVoice = "Exhale and let insights crystallize",
            restInstruction = "Rest in the creative field. Ideas will come effortlessly.",
            motivational = "You are accessing levels of mind that most people never reach.",
            musicFile = "morning_forest"
        ),

        "focus_neutral_beginner" to BreathingInstructions(
            welcome = "Welcome. Neutral is perfect for focus practice. No emotional interference. Let's build concentration from this clean slate.",
            inhaleInstruction = "Breathe in and gently pick one thing to focus on...",
            inhaleVoice = "Breathe in and gently select your focus point",
            holdInstruction = "Hold your attention on that one thing. When it wanders, bring it back.",
            holdVoice = "Hold... when mind wanders, gently return",
            exhaleInstruction = "Breathe out. If any thought arose, release it now.",
            exhaleVoice = "Breathe out any wandering thoughts",
            restInstruction = "Rest. You practiced returning attention — that IS the exercise.",
            motivational = "Each time you return your wandering attention, your focus muscle grows stronger.",
            musicFile = "morning_forest"
        ),

        "focus_neutral_intermediate" to BreathingInstructions(
            welcome = "Welcome. From neutral mind, we build Dharana — the yogic art of concentration. Each breath is a rep in your mental gym.",
            inhaleInstruction = "Inhale and narrow your attention beam like a spotlight...",
            inhaleVoice = "Inhale and narrow your attention like a spotlight",
            holdInstruction = "Hold the spotlight steady on your chosen object.",
            holdVoice = "Hold the spotlight... absolutely steady",
            exhaleInstruction = "Exhale without losing the spotlight. This is the test.",
            exhaleVoice = "Exhale... keep the spotlight absolutely steady",
            restInstruction = "Rest. Your spotlight is becoming a laser.",
            motivational = "Dharana is mastery of attention. You are becoming a master.",
            musicFile = "morning_forest"
        ),

        "focus_neutral_advanced" to BreathingInstructions(
            welcome = "Welcome. Today we practice Dhyana — meditation as the unbroken flow of concentration. Beyond focus into pure awareness.",
            inhaleInstruction = "Inhale into witness consciousness — aware of awareness itself...",
            inhaleVoice = "Inhale into awareness of awareness",
            holdInstruction = "Hold and rest as the observer — not the thinker.",
            holdVoice = "Hold as pure observer... not the thinker",
            exhaleInstruction = "Exhale and dissolve the boundary between observer and observed.",
            exhaleVoice = "Exhale... dissolve the observer and observed",
            restInstruction = "Rest in samadhi — pure undivided awareness.",
            motivational = "In this state, you and focus are not two things. You are focus itself.",
            musicFile = "morning_forest"
        ),

        "focus_excited_beginner" to BreathingInstructions(
            welcome = "Welcome. That excitement you feel is incredible fuel! Let's harness it into the kind of focused energy that creates amazing things.",
            inhaleInstruction = "Breathe in and feel the excitement organizing into clear purpose...",
            inhaleVoice = "Breathe in... excitement organizing into clear purpose",
            holdInstruction = "Hold and see your goal clearly in front of you.",
            holdVoice = "Hold... see your goal clearly",
            exhaleInstruction = "Breathe out anything that isn't aligned with your goal.",
            exhaleVoice = "Breathe out what doesn't align with your goal",
            restInstruction = "Rest. You are now a guided missile of focused intention.",
            motivational = "Excited purpose is the most powerful force in human achievement.",
            musicFile = "morning_forest"
        ),

        "focus_excited_intermediate" to BreathingInstructions(
            welcome = "Welcome. High-energy focus is a rare and powerful state. Let's structure it so it becomes sustained rather than scattered.",
            inhaleInstruction = "Inhale and structure your excitement into a clear sequence of actions...",
            inhaleVoice = "Inhale and structure your excitement into sequence",
            holdInstruction = "Hold and prioritize — what is the single most important next step?",
            holdVoice = "Hold... what is the single most important step?",
            exhaleInstruction = "Exhale all the exciting possibilities except the chosen one.",
            exhaleVoice = "Exhale all possibilities except the one you chose",
            restInstruction = "Rest. Focused execution is beginning.",
            motivational = "You have chosen. Now execute with complete focus. Nothing else exists.",
            musicFile = "morning_forest"
        ),

        "focus_excited_advanced" to BreathingInstructions(
            welcome = "Welcome. Advanced focus combined with excitement creates the state athletes call the zone. Let's engineer flow state deliberately.",
            inhaleInstruction = "Inhale and set the challenge slightly above your current skill...",
            inhaleVoice = "Inhale... challenge set at the edge of your ability",
            holdInstruction = "Hold in the challenge-skill sweet spot. This IS flow state.",
            holdVoice = "Hold in the sweet spot... this is flow",
            exhaleInstruction = "Exhale self-consciousness. In flow, there is no performer — only performance.",
            exhaleVoice = "Exhale self-consciousness... only performance remains",
            restInstruction = "Rest. Flow is effortless. Let it carry you.",
            motivational = "You are in flow. Time will dissolve. Work will become art.",
            musicFile = "morning_forest"
        ),

        // ════════════════════════════════════════════════════════
        // SLEEP — ALL MOODS
        // ════════════════════════════════════════════════════════

        "sleep_anxious_beginner" to BreathingInstructions(
            welcome = "Welcome. The night can feel frightening when anxiety is present. You are safe. Your bed is a sanctuary. Let's breathe you into peaceful rest.",
            inhaleInstruction = "Breathe in slowly... let your body get heavier and heavier...",
            inhaleVoice = "Breathe in slowly... feel your body getting heavier",
            holdInstruction = "Hold softly. Feel the mattress supporting every part of you.",
            holdVoice = "Hold... feel supported completely by your bed",
            exhaleInstruction = "Breathe out worry. Tomorrow will take care of itself.",
            exhaleVoice = "Breathe out all worry... tomorrow will handle itself",
            restInstruction = "Rest. Sleep is close. You are safe.",
            motivational = "You are drifting safely into the most healing sleep of your life.",
            musicFile = "ocean_sleep"
        ),

        "sleep_anxious_intermediate" to BreathingInstructions(
            welcome = "Welcome. You know anxiety hijacks sleep by keeping the nervous system alert. Tonight we use 4-7-8 breathing to deliberately trigger the relaxation response.",
            inhaleInstruction = "4-7-8 inhale — breathe in for 4 counts only through nose...",
            inhaleVoice = "Inhale through nose for four counts",
            holdInstruction = "Hold for 7 counts. Your parasympathetic system is activating.",
            holdVoice = "Hold for seven... parasympathetic system activating",
            exhaleInstruction = "Exhale through mouth for 8 counts with a whooshing sound.",
            exhaleVoice = "Whooshing exhale for eight counts",
            restInstruction = "Rest. Your nervous system is switching from alert to rest mode.",
            motivational = "Three cycles of this and you will feel irresistibly sleepy. Trust it.",
            musicFile = "ocean_sleep"
        ),

        "sleep_anxious_advanced" to BreathingInstructions(
            welcome = "Welcome. Tonight we practice yoga nidra breathing — conscious entry into the hypnagogic state, the doorway between waking and sleep.",
            inhaleInstruction = "Breathe in and rotate awareness through your body systematically...",
            inhaleVoice = "Breathe in and bring awareness to your right hand",
            holdInstruction = "Hold awareness there. Feel the sensation completely.",
            holdVoice = "Hold awareness... feel every sensation",
            exhaleInstruction = "Breathe out and release awareness of that body part.",
            exhaleVoice = "Breathe out and release... moving to the next area",
            restInstruction = "Rest. You are entering the hypnagogic threshold.",
            motivational = "You are crossing the threshold into conscious sleep. Surrender.",
            musicFile = "ocean_sleep"
        ),

        "sleep_sad_beginner" to BreathingInstructions(
            welcome = "Welcome. Sadness and sleeplessness are old companions. Tonight let's be extra gentle. You deserve a peaceful night more than anyone.",
            inhaleInstruction = "Breathe in and imagine wrapping yourself in warm golden light...",
            inhaleVoice = "Breathe in warm golden light wrapping around you",
            holdInstruction = "Hold and let the warmth dissolve the sadness bit by bit.",
            holdVoice = "Hold... warmth dissolving the sadness",
            exhaleInstruction = "Breathe out the sadness like dark smoke leaving your body.",
            exhaleVoice = "Breathe out dark smoke... the sadness is leaving",
            restInstruction = "Rest. You are becoming lighter. Sleep is coming to heal you.",
            motivational = "Sleep is the greatest healer. Let it restore you tonight.",
            musicFile = "ocean_sleep"
        ),

        "sleep_sad_intermediate" to BreathingInstructions(
            welcome = "Welcome. Grief and loss disrupt sleep at a neurological level. Tonight we use coherent breathing to regulate your heart rhythm and invite deep rest.",
            inhaleInstruction = "Coherent breathing — inhale for exactly 5 seconds...",
            inhaleVoice = "Inhale for five seconds... heart coherence building",
            holdInstruction = "Brief hold. Heart rate variability harmonizing.",
            holdVoice = "Hold briefly... heart harmonizing",
            exhaleInstruction = "Exhale for exactly 5 seconds. Heart and breath in perfect sync.",
            exhaleVoice = "Exhale for five seconds... heart and breath in sync",
            restInstruction = "Rest. Your heart rhythm is now coherent and calm.",
            motivational = "Heart coherence activates healing during sleep. You are healing.",
            musicFile = "ocean_sleep"
        ),

        "sleep_sad_advanced" to BreathingInstructions(
            welcome = "Welcome. Tonight we practice the ancient Tibetan sleep yoga — using breath to consciously prepare the mind-body for healing rest.",
            inhaleInstruction = "Inhale and visualize light entering through the crown of your head...",
            inhaleVoice = "Inhale light through the crown chakra",
            holdInstruction = "Hold and let the light travel down through your central channel.",
            holdVoice = "Hold... light traveling through the central channel",
            exhaleInstruction = "Exhale darkness from the base of the spine downward.",
            exhaleVoice = "Exhale darkness downward from the base",
            restInstruction = "Rest. You are a column of pure light. Sleep will be luminous.",
            motivational = "You are entering healing sleep as a practitioner. Rest deeply.",
            musicFile = "ocean_sleep"
        ),

        "sleep_angry_beginner" to BreathingInstructions(
            welcome = "Welcome. It's hard to sleep when you are angry. Let's release the charge from your body so sleep can come. You don't have to solve anything tonight.",
            inhaleInstruction = "Breathe in cold, cooling air through your nose...",
            inhaleVoice = "Breathe in cool air... feel it cool the anger",
            holdInstruction = "Hold and imagine ice water flowing through your veins.",
            holdVoice = "Hold... ice cooling the heat of anger",
            exhaleInstruction = "Breathe out the heat and charge. You can deal with it tomorrow.",
            exhaleVoice = "Breathe out all the heat... tomorrow will handle it",
            restInstruction = "Rest. The anger is discharging. Sleep is possible now.",
            motivational = "You released the charge. Sleep will process what anger cannot.",
            musicFile = "ocean_sleep"
        ),

        "sleep_angry_intermediate" to BreathingInstructions(
            welcome = "Welcome. Anger keeps cortisol elevated — making sleep physiologically impossible. Tonight's breath practice is specifically designed to lower cortisol rapidly.",
            inhaleInstruction = "Extended exhale breathing — inhale for 4...",
            inhaleVoice = "Inhale for four... cortisol decreasing",
            holdInstruction = "Brief hold. Cortisol receptors are downregulating.",
            holdVoice = "Brief hold... cortisol coming down",
            exhaleInstruction = "Exhale for 8 — double the inhale. This is the cortisol protocol.",
            exhaleVoice = "Exhale for eight... this brings cortisol down rapidly",
            restInstruction = "Rest. Sleep hormones are rising as stress hormones fall.",
            motivational = "Melatonin is rising. Cortisol is falling. Sleep is biochemically inevitable.",
            musicFile = "ocean_sleep"
        ),

        "sleep_angry_advanced" to BreathingInstructions(
            welcome = "Welcome. Advanced practitioners use Sheetali breathing — the cooling breath — to rapidly discharge anger and prepare for deep sleep.",
            inhaleInstruction = "Sheetali — curl tongue like a tube, inhale cool air through it...",
            inhaleVoice = "Sheetali... curl tongue and inhale cool air",
            holdInstruction = "Hold the cooling breath. Feel it neutralizing the heat of anger.",
            holdVoice = "Hold the cool... anger neutralizing",
            exhaleInstruction = "Exhale through the nose slowly. Completely cool now.",
            exhaleVoice = "Exhale through nose... completely cool",
            restInstruction = "Rest. You have used the ancient cooling breath. Sleep will be deep.",
            motivational = "Ancient wisdom has cooled you. Now sleep will restore you completely.",
            musicFile = "ocean_sleep"
        ),

        "sleep_happy_beginner" to BreathingInstructions(
            welcome = "Welcome. What a beautiful night to sleep — you feel good! Let's carry this happiness right into your dreams.",
            inhaleInstruction = "Breathe in this lovely feeling and store it deep in your heart...",
            inhaleVoice = "Breathe in this good feeling... store it in your heart",
            holdInstruction = "Hold and feel gratitude for this wonderful day.",
            holdVoice = "Hold... feel gratitude for today",
            exhaleInstruction = "Breathe out slowly and let your body sink into the bed.",
            exhaleVoice = "Breathe out slowly... sink into perfect rest",
            restInstruction = "Rest. Happy dreams await you.",
            motivational = "You are carrying happiness into sleep. Your dreams will be vivid and joyful.",
            musicFile = "ocean_sleep"
        ),

        "sleep_happy_intermediate" to BreathingInstructions(
            welcome = "Welcome. Falling asleep happy is the best gift you can give your subconscious mind. Tonight we seal this positive state into your nervous system.",
            inhaleInstruction = "Inhale and review three things you are grateful for today...",
            inhaleVoice = "Inhale and think of three things you are grateful for",
            holdInstruction = "Hold and feel deep appreciation for each one.",
            holdVoice = "Hold... feel appreciation for each blessing",
            exhaleInstruction = "Exhale and release the day with complete contentment.",
            exhaleVoice = "Exhale with complete contentment for the day",
            restInstruction = "Rest. Gratitude is sealing your nervous system in peace.",
            motivational = "You are programming your subconscious for more of what made you happy today.",
            musicFile = "ocean_sleep"
        ),

        "sleep_happy_advanced" to BreathingInstructions(
            welcome = "Welcome. Tonight we use Yoga Nidra with sankalpa — planting a positive intention into the fertile ground of the hypnagogic state.",
            inhaleInstruction = "Inhale and formulate your sankalpa — a short, positive intention...",
            inhaleVoice = "Inhale and form your sankalpa... I am...",
            holdInstruction = "Hold and plant the sankalpa in your subconscious deeply.",
            holdVoice = "Hold... plant the seed deeply in the subconscious",
            exhaleInstruction = "Exhale and surrender to sleep — the sankalpa is planted.",
            exhaleVoice = "Exhale... the seed is planted, surrender to sleep",
            restInstruction = "Rest. Your subconscious will work on this intention all night.",
            motivational = "Sleep is now a sacred workshop. Your subconscious is creating.",
            musicFile = "ocean_sleep"
        ),

        "sleep_neutral_beginner" to BreathingInstructions(
            welcome = "Welcome. You feel okay — and that's a great place to fall asleep from. Let's use simple, easy breathing to drift off naturally.",
            inhaleInstruction = "Breathe in... let your eyes feel heavy...",
            inhaleVoice = "Breathe in... let your eyes feel heavy",
            holdInstruction = "Hold... let your whole body feel heavy.",
            holdVoice = "Hold... body getting heavier",
            exhaleInstruction = "Breathe out... let go of the day completely.",
            exhaleVoice = "Breathe out... let the day go completely",
            restInstruction = "Rest... you are almost asleep...",
            motivational = "Sleep is coming softly for you. Just keep breathing.",
            musicFile = "ocean_sleep"
        ),

        "sleep_neutral_intermediate" to BreathingInstructions(
            welcome = "Welcome. From neutral mind, we can enter sleep efficiently and deeply. Tonight's practice optimizes your sleep architecture.",
            inhaleInstruction = "Breathe in and consciously relax each muscle from feet upward...",
            inhaleVoice = "Inhale and relax from feet upward",
            holdInstruction = "Hold and check — are your shoulders relaxed? Your jaw?",
            holdVoice = "Hold... check shoulders, jaw, forehead... relax them",
            exhaleInstruction = "Exhale and drop into delta waves — deep sleep is activating.",
            exhaleVoice = "Exhale into delta waves... deep sleep activating",
            restInstruction = "Rest. Your body is orchestrating perfect sleep architecture.",
            motivational = "Delta wave sleep is your deepest healer. You are entering it now.",
            musicFile = "ocean_sleep"
        ),

        "sleep_neutral_advanced" to BreathingInstructions(
            welcome = "Welcome. Tonight we practice conscious sleep entry — maintaining a thread of awareness as the body falls asleep. This is the beginning of lucid dreaming.",
            inhaleInstruction = "Inhale and maintain witness awareness as the body relaxes...",
            inhaleVoice = "Inhale and maintain witness... body relaxing below you",
            holdInstruction = "Hold — the body is asleep, the witness remains awake.",
            holdVoice = "Hold... body sleeping, awareness remaining",
            exhaleInstruction = "Exhale — follow the breath into the dreamspace.",
            exhaleVoice = "Follow the exhale into the dreamspace",
            restInstruction = "Rest as pure awareness. The dream is beginning.",
            motivational = "You are entering conscious sleep. The dreaming world awaits your awareness.",
            musicFile = "ocean_sleep"
        ),

        "sleep_excited_beginner" to BreathingInstructions(
            welcome = "Welcome. You are too excited to sleep — that is perfectly normal. Let's use a special slowing breath to gradually ease your nervous system down.",
            inhaleInstruction = "Breathe in for 3 counts — short inhale...",
            inhaleVoice = "Short inhale for three",
            holdInstruction = "Hold for 2. Feel yourself starting to slow down.",
            holdVoice = "Hold two... slowing down",
            exhaleInstruction = "Exhale for 6 counts — long slow exhale. This is key.",
            exhaleVoice = "Long slow exhale for six... slowing everything down",
            restInstruction = "Rest. With each cycle you are slowing 10% more.",
            motivational = "Your excitement is transforming into the sweetest, most deserved sleep.",
            musicFile = "ocean_sleep"
        ),

        "sleep_excited_intermediate" to BreathingInstructions(
            welcome = "Welcome. High arousal before sleep disrupts sleep quality. Tonight we deliberately induce physiological deceleration using extended exhale breathing.",
            inhaleInstruction = "Inhale for 4 — the last intake of excited energy...",
            inhaleVoice = "Inhale four... receiving the last of the day's energy",
            holdInstruction = "Hold 2 — this is the turning point from arousal to rest.",
            holdVoice = "Hold two... turning from arousal to rest",
            exhaleInstruction = "Exhale for 8 — releasing all the day's accumulated energy.",
            exhaleVoice = "Exhale eight... releasing all of the day's energy",
            restInstruction = "Rest. The deceleration is complete. Sleep is physiologically ready.",
            motivational = "Your neurotransmitters are shifting from dopamine to serotonin to melatonin.",
            musicFile = "ocean_sleep"
        ),

        "sleep_excited_advanced" to BreathingInstructions(
            welcome = "Welcome. Advanced practitioners can use excitement as a springboard for vivid, productive dreaming. Let's direct this energy consciously into sleep.",
            inhaleInstruction = "Inhale and set an intention for your dream tonight...",
            inhaleVoice = "Inhale and set your dream intention",
            holdInstruction = "Hold and see the dream scenario in vivid detail.",
            holdVoice = "Hold... see the dream in vivid detail",
            exhaleInstruction = "Exhale and enter the dream. The excitement fuels it.",
            exhaleVoice = "Exhale into the dream... let excitement fuel it",
            restInstruction = "Rest. You are entering an intentional, vivid dream state.",
            motivational = "Your excited mind will create extraordinary dreams tonight.",
            musicFile = "ocean_sleep"
        ),

        // ════════════════════════════════════════════════════════
        // HAPPY (Feel Happier) — KEY COMBINATIONS
        // ════════════════════════════════════════════════════════

        "happy_anxious_beginner" to BreathingInstructions(
            welcome = "Welcome. Anxiety is just happiness turned inside out. Let's turn it back. One breath, one smile at a time.",
            inhaleInstruction = "Breathe in and let the corners of your lips gently rise...",
            inhaleVoice = "Breathe in and let a tiny smile form",
            holdInstruction = "Hold that smile. Notice how the anxiety loses power.",
            holdVoice = "Hold the smile... anxiety losing power",
            exhaleInstruction = "Breathe out the anxiety and keep the smile.",
            exhaleVoice = "Breathe out anxiety, keep the smile",
            restInstruction = "Rest. Happiness is already inside you — you are uncovering it.",
            motivational = "Joy is your natural state. Anxiety is just a temporary visitor.",
            musicFile = "silver_river_keys"
        ),

        "happy_sad_beginner" to BreathingInstructions(
            welcome = "Welcome. Sadness and happiness can coexist — like rain and sunshine making a rainbow. Let's breathe in some sunshine today.",
            inhaleInstruction = "Breathe in and remember one moment that made you smile...",
            inhaleVoice = "Breathe in and remember one joyful moment",
            holdInstruction = "Hold that memory. Feel it in your body.",
            holdVoice = "Hold... feel the joy memory in your body",
            exhaleInstruction = "Breathe out the sadness. The memory of joy is real too.",
            exhaleVoice = "Breathe out sadness... joy is equally real",
            restInstruction = "Rest. You are creating a rainbow of emotions.",
            motivational = "You hold within you every happiness you have ever felt. It is still there.",
            musicFile = "silver_river_keys"
        ),

        "happy_neutral_beginner" to BreathingInstructions(
            welcome = "Welcome. From neutral, we can consciously cultivate joy. This is the most underrated skill in the world — let's practice it.",
            inhaleInstruction = "Breathe in and create a feeling of appreciation for being alive...",
            inhaleVoice = "Breathe in appreciation for being alive right now",
            holdInstruction = "Hold and let that appreciation grow into something warmer.",
            holdVoice = "Hold... let appreciation grow into warmth",
            exhaleInstruction = "Breathe out the warmth as a gift to the world around you.",
            exhaleVoice = "Breathe out warmth as a gift to the world",
            restInstruction = "Rest. You are generating happiness from the inside out.",
            motivational = "You just proved that happiness is a skill, not a circumstance.",
            musicFile = "silver_river_keys"
        ),

        // ════════════════════════════════════════════════════════
        // CALM — KEY COMBINATIONS
        // ════════════════════════════════════════════════════════

        "calm_anxious_beginner" to BreathingInstructions(
            welcome = "Welcome. Calm is not the absence of anxiety — it is a deeper layer beneath it. Let's breathe down to that layer right now.",
            inhaleInstruction = "Breathe in and sink deeper than the surface anxiety...",
            inhaleVoice = "Breathe in and sink beneath the anxiety",
            holdInstruction = "Hold and find the quiet that exists underneath the noise.",
            holdVoice = "Hold... find the quiet beneath the noise",
            exhaleInstruction = "Breathe out the surface anxiety. The calm below is untouched.",
            exhaleVoice = "Breathe out the surface... the calm beneath is untouched",
            restInstruction = "Rest in the unshakeable calm at your core.",
            motivational = "The eye of the storm is always perfectly calm. You are finding it.",
            musicFile = "deep_forest"
        ),

        "calm_angry_beginner" to BreathingInstructions(
            welcome = "Welcome. Anger is a storm on the surface of the ocean. Beneath it, the ocean floor is completely still. Let's go there.",
            inhaleInstruction = "Breathe in and imagine diving deeper than the storm...",
            inhaleVoice = "Breathe in and dive beneath the storm of anger",
            holdInstruction = "Hold at the ocean floor. It is quiet here. Always.",
            holdVoice = "Hold at the floor of the ocean... perfect stillness",
            exhaleInstruction = "Breathe out from the stillness. You don't need to be the storm.",
            exhaleVoice = "Breathe out from the stillness",
            restInstruction = "Rest at the calm floor. The storm above means nothing here.",
            motivational = "You are vaster than any anger. You contain it. It does not contain you.",
            musicFile = "deep_forest"
        ),

        "calm_neutral_intermediate" to BreathingInstructions(
            welcome = "Welcome. From neutral mind, we can access the deepest calm — not forced calm, but natural, effortless equanimity. Let's find it.",
            inhaleInstruction = "Breathe in without trying to make anything happen...",
            inhaleVoice = "Breathe in without effort... without agenda",
            holdInstruction = "Hold without controlling. Just be with what is.",
            holdVoice = "Hold without controlling",
            exhaleInstruction = "Exhale without pushing. Let the breath release itself.",
            exhaleVoice = "Let the exhale release itself",
            restInstruction = "Rest. This is wu wei — effortless action. Natural calm.",
            motivational = "You have found the Tao — the natural flow beneath all effort.",
            musicFile = "deep_forest"
        ),

        // ════════════════════════════════════════════════════════
        // MINDFUL — KEY COMBINATIONS
        // ════════════════════════════════════════════════════════

        "mindful_anxious_beginner" to BreathingInstructions(
            welcome = "Welcome. Mindfulness is the cure for anxiety because anxiety lives in the future and mindfulness lives in now. Let's come home to now.",
            inhaleInstruction = "Notice the sensation of air entering your nostrils right now...",
            inhaleVoice = "Notice the cool air entering your nostrils right now",
            holdInstruction = "Hold and notice the sensation of fullness in your chest.",
            holdVoice = "Hold... notice the sensation of fullness",
            exhaleInstruction = "Notice the warm air leaving. The sensation of release.",
            exhaleVoice = "Notice the warm air leaving... sensation of release",
            restInstruction = "Rest and notice the natural pause. This is the present moment.",
            motivational = "In this present moment, there is no anxiety. Only this. Only now.",
            musicFile = "ocean_waves"
        ),

        "mindful_neutral_intermediate" to BreathingInstructions(
            welcome = "Welcome. Pure mindfulness practice — no goal except complete presence. Every sensation is your meditation object. Let's begin.",
            inhaleInstruction = "Notice without labeling — the temperature, the rhythm, the depth...",
            inhaleVoice = "Notice everything about this inhale without labeling it",
            holdInstruction = "Notice the held breath without judging it as good or bad.",
            holdVoice = "Notice the held breath without judgment",
            exhaleInstruction = "Notice the exhale as if you have never exhaled before.",
            exhaleVoice = "Notice this exhale as if it is the first exhale ever",
            restInstruction = "Notice the space between breaths. That space is mindfulness.",
            motivational = "You are not practicing mindfulness. You are being mindfulness.",
            musicFile = "ocean_waves"
        ),

        "mindful_excited_advanced" to BreathingInstructions(
            welcome = "Welcome. Mindful excitement is the state of wonder — like a child who sees everything as new and miraculous. Let's practice this.",
            inhaleInstruction = "Breathe in with the curiosity of someone who has never breathed before...",
            inhaleVoice = "Breathe in with fresh eyes... as if for the very first time",
            holdInstruction = "Hold and marvel at the miracle of being alive and conscious.",
            holdVoice = "Hold... marvel at the miracle of consciousness",
            exhaleInstruction = "Exhale in wonder at the extraordinary fact of your existence.",
            exhaleVoice = "Exhale in wonder at your extraordinary existence",
            restInstruction = "Rest in beginner's mind. Everything is new. Everything is a miracle.",
            motivational = "You are a conscious being in an infinite universe. That is the greatest miracle.",
            musicFile = "ocean_waves"
        )
    )

    // ── Lookup function ────────────────────────────────────────

    fun get(goal: String, mood: String, level: String): BreathingInstructions {
        val key = "${goal.lowercase()}_${mood.lowercase()}_${level.lowercase()}"

        // Exact match first
        instructions[key]?.let { return it }

        // Fallback 1: same goal + mood, any level
        val fallbackLevel = listOf("beginner", "intermediate", "advanced")
            .firstOrNull { instructions["${goal.lowercase()}_${mood.lowercase()}_$it"] != null }
        fallbackLevel?.let {
            return instructions["${goal.lowercase()}_${mood.lowercase()}_$it"]!!
        }

        // Fallback 2: same goal, neutral mood
        val fallbackMood = listOf("neutral", "anxious", "sad", "happy", "angry", "excited")
            .firstOrNull { instructions["${goal.lowercase()}_${it}_beginner"] != null }
        fallbackMood?.let {
            return instructions["${goal.lowercase()}_${it}_beginner"]!!
        }

        // Fallback 3: default calm session
        return BreathingInstructions(
            welcome = "Welcome. Find a comfortable position, close your eyes, and take a deep breath. Your meditation journey begins now.",
            inhaleInstruction = "Slowly inhale through your nose...",
            inhaleVoice = "Breathe in peace and stillness",
            holdInstruction = "Hold your breath gently...",
            holdVoice = "Hold... rest in the silence",
            exhaleInstruction = "Slowly exhale through your mouth...",
            exhaleVoice = "Breathe out slowly and completely",
            restInstruction = "Relax and prepare for next breath...",
            motivational = "You are doing beautifully. Keep breathing.",
            musicFile = "deep_forest"
        )
    }
}