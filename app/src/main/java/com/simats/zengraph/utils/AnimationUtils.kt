package com.simats.zengraph.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import androidx.core.view.ViewCompat

object AnimationUtils {

    /**
     * Applies a premium 3D rotation effect to a view on the Y-axis.
     * Perfect for hover-like effects on cards.
     */
    fun apply3DRotation(view: View, rotationY: Float = 15f) {
        view.cameraDistance = view.height * 10f
        ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0f, rotationY, 0f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * Applies a spring-based scale pop animation.
     * Use this for button clicks or item selection.
     */
    fun applyScalePop(view: View, scale: Float = 1.05f) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, scale, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, scale, 1f)
        
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 300
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
    }

    /**
     * Adds a subtle 3D tilt effect based on view movement or touch.
     */
    fun apply3DTilt(view: View, tiltX: Float, tiltY: Float) {
        view.cameraDistance = view.height * 20f
        view.rotationX = tiltX
        view.rotationY = tiltY
    }

    /**
     * Starts a continuous "breathing" floating animation for a 3D effect.
     */
    fun startFloatingAnimation(view: View, offset: Float = 10f, duration: Long = 2000) {
        val translateY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -offset, offset).apply {
            setDuration(duration)
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        val rotateX = ObjectAnimator.ofFloat(view, View.ROTATION_X, -2f, 2f).apply {
            setDuration(duration * 1.5.toLong())
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(translateY, rotateX)
            start()
        }
    }

    /**
     * Animates a view with a 3D entrance (Scale + Rotate + Alpha).
     */
    fun apply3DEntrance(view: View, delay: Long = 0) {
        view.alpha = 0f
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.rotationX = -20f
        
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .rotationX(0f)
            .setDuration(600)
            .setStartDelay(delay)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
