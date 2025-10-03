package com.yourpackage

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radarColor: Int = Color.GREEN

    private val circlePaint: Paint
    private val sweepPaint: Paint

    private var rotationAngle = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView)
        radarColor = typedArray.getColor(R.styleable.RadarView_radarColor, Color.GREEN)
        typedArray.recycle()

        circlePaint = Paint().apply {
            color = radarColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        sweepPaint = Paint().apply {
            isAntiAlias = true
        }

        val animator = ValueAnimator.ofFloat(0f, 360f).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 3000 // 3 seconds for a full rotation
            addUpdateListener { animation ->
                rotationAngle = animation.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radius = Math.min(width, height) / 2

        // Draw concentric circles
        for (i in 1..4) {
            canvas.drawCircle(centerX, centerY, radius * (i.toFloat() / 4), circlePaint)
        }

        // Draw rotating sweep
        val sweepShader: Shader = SweepGradient(
            centerX, centerY,
            intArrayOf(Color.TRANSPARENT, radarColor),
            floatArrayOf(0f, 30f / 360f)
        )

        sweepPaint.shader = sweepShader
        canvas.save()
        canvas.rotate(rotationAngle, centerX, centerY)
        canvas.drawCircle(centerX, centerY, radius, sweepPaint)
        canvas.restore()
    }
}
