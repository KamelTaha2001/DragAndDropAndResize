package mobile.computing.draganddroppractice

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import kotlin.math.pow

class MyResizableView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circleRadius = 20f

    private var initialTopCircleY = 0f
    private var currentTopCircleY = 0f
    private var initialBottomCircleY = 0f
    private var currentBottomCircleY = 0f
    private var initialHeight = 0f
    private var initialXPosition = 0f
    private var initialYPosition = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    init {
        initialHeight = height.toFloat()
        rectPaint.color = Color.DKGRAY
        circlePaint.color = Color.WHITE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentTopCircleY = circleRadius
        currentBottomCircleY = height - circleRadius

        if (initialTopCircleY != 0f) {         // If currently dragging the top circle
            y += (oldh - h).toFloat()        // Shift the view up to create an illusion of increasing the size from the top
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(width.toFloat(), 0f, 0f, height.toFloat(), rectPaint)
        canvas.drawCircle(width.toFloat() / 2, currentTopCircleY, circleRadius, circlePaint)
        canvas.drawCircle(width.toFloat() / 2, currentBottomCircleY, circleRadius, circlePaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInsideTopCircle(event.x, event.y)) {
                    initialTopCircleY = currentTopCircleY
                    initialHeight = height.toFloat()
                }
                if (isInsideBottomCircle(event.x, event.y)) {
                    initialBottomCircleY = currentBottomCircleY
                    initialHeight = height.toFloat()
                } else {
                    initialXPosition = x
                    initialYPosition = y
                    offsetX = event.rawX - x
                    offsetY = event.rawY - y
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (initialTopCircleY != 0f) {
                    handleCircleTouchMove(event, CirclePosition.TOP)
                } else if (initialBottomCircleY != 0f) {
                    handleCircleTouchMove(event, CirclePosition.BOTTOM)
                } else {
                    handleRectTouchMove(event)
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                initialTopCircleY = 0f
                initialBottomCircleY = 0f
                initialHeight = height.toFloat()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleRectTouchMove(event: MotionEvent) {
        val newX = event.rawX - offsetX
        val newY = event.rawY - offsetY
        // Update view's position within the FrameLayout
        val newLayoutParams = layoutParams as FrameLayout.LayoutParams
        val p = parent as FrameLayout
        newLayoutParams.leftMargin = newX.toInt().coerceIn(0, p.width - width)
        newLayoutParams.topMargin = newY.toInt().coerceIn(0, p.height - height)
        layoutParams = newLayoutParams
    }

    private fun handleCircleTouchMove(event: MotionEvent, circlePosition: CirclePosition) {
        val newHeight = when (circlePosition) {
            CirclePosition.TOP -> {
                val delta = event.y - initialTopCircleY
                initialHeight - delta
            }

            CirclePosition.BOTTOM -> {
                val delta = initialBottomCircleY - event.y
                initialHeight - delta
            }
        }

        val newHeightInt = newHeight.toInt().coerceIn(100, 700)
        layoutParams.height = newHeightInt
        requestLayout()
    }

    private fun isInsideTopCircle(x: Float, y: Float): Boolean {
        val centerX = width / 2f
        return (x - centerX).pow(2) + (y - currentTopCircleY).pow(2) <= circleRadius.pow(2)
    }

    private fun isInsideBottomCircle(x: Float, y: Float): Boolean {
        val centerX = width / 2f
        return (x - centerX).pow(2) + (y - currentBottomCircleY).pow(2) <= circleRadius.pow(2)
    }
}