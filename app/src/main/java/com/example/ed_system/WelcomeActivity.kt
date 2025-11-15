package com.example.ed_system

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_screen)

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        startWaveAnimation(findViewById(R.id.wave1), swayDp = 16f, bobDp = 8f, swaySpeed = 1800L, bobSpeed = 1300L)
        startWaveAnimation(findViewById(R.id.wave2), swayDp = 12f, bobDp = 6f, swaySpeed = 2300L, bobSpeed = 1700L)
        startWaveAnimation(findViewById(R.id.wave3), swayDp = 8f, bobDp = 4f, swaySpeed = 3000L, bobSpeed = 2200L)
    }

    private fun startWaveAnimation(
        wave: View,
        swayDp: Float,
        bobDp: Float,
        swaySpeed: Long,
        bobSpeed: Long
    ) {
        val baseX = wave.translationX
        val baseY = wave.translationY

        val sway = ObjectAnimator.ofFloat(
            wave,
            View.TRANSLATION_X,
            baseX,
            baseX - wave.dp(swayDp)
        ).apply {
            duration = swaySpeed
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = LinearInterpolator()
        }

        val bob = ObjectAnimator.ofFloat(
            wave,
            View.TRANSLATION_Y,
            baseY,
            baseY - wave.dp(bobDp)
        ).apply {
            duration = bobSpeed
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(sway, bob)
            start()
        }
    }

    private fun View.dp(value: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
}