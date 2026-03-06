package com.simats.zengraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class StarBackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val stars = mutableListOf<Star>()
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    private val random = Random()

    private class Star(var x: Float, var y: Float, var radius: Float, var alpha: Int, val speed: Float, val layer: Int)

    init {
        // Initialize background color
        setBackgroundColor(Color.parseColor("#0A0E1A"))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        stars.clear()
        for (i in 0 until 150) {
            val layer = if (i < 50) 1 else if (i < 100) 2 else 3
            stars.add(
                Star(
                    random.nextFloat() * w,
                    random.nextFloat() * h,
                    random.nextFloat() * (layer * 1f) + 0.5f,
                    random.nextInt(150) + 50,
                    (random.nextFloat() * 0.2f + 0.05f) * layer,
                    layer
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw Nebula Glow
        paint.style = Paint.Style.FILL
        for (i in 0..2) {
            paint.setARGB(22, 0, 210, 255) // Electric Cyan (#00D2FF)
            canvas.drawCircle(width / 2f, height / 2f, (width / (i + 1.5f)), paint)
        }

        for (star in stars) {
            paint.color = Color.WHITE
            paint.alpha = star.alpha
            canvas.drawCircle(star.x, star.y, star.radius, paint)

            // Update star position (moving downwards)
            star.y += star.speed
            if (star.y > height) {
                star.y = 0f
                star.x = random.nextFloat() * width
            }
            
            // Subtle twinkling based on layer
            if (random.nextFloat() > (0.95f + (star.layer * 0.01f))) {
                star.alpha = random.nextInt(150) + 50
            }
        }
        invalidate()
    }
}
