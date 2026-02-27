package com.mobilegiants.megila.custom_views

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.min

class TouchImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val imageMatrix2 = Matrix()
    private val m = FloatArray(9)

    private var mode = NONE
    private val last = PointF()
    private val start = PointF()
    private var minScale = 1f
    private var maxScale = 3f

    private var viewWidth = 0
    private var viewHeight = 0
    private var saveScale = 1f
    private var origWidth = 0f
    private var origHeight = 0f
    private var oldMeasuredWidth = 0
    private var oldMeasuredHeight = 0

    private val scaleDetector: ScaleGestureDetector

    init {
        isClickable = true
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        imageMatrix = imageMatrix2
        scaleType = ScaleType.MATRIX

        setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            val curr = PointF(event.x, event.y)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    last.set(curr)
                    start.set(last)
                    mode = DRAG
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        val deltaX = curr.x - last.x
                        val deltaY = curr.y - last.y
                        val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(), origWidth * saveScale)
                        val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(), origHeight * saveScale)
                        imageMatrix2.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last.set(curr.x, curr.y)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    mode = NONE
                    val xDiff = abs(curr.x - start.x).toInt()
                    val yDiff = abs(curr.y - start.y).toInt()
                    if (xDiff < CLICK && yDiff < CLICK) performClick()
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                }
            }
            imageMatrix = imageMatrix2
            invalidate()
            true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = detector.scaleFactor
            val origScale = saveScale
            saveScale *= scaleFactor
            if (saveScale > maxScale) {
                saveScale = maxScale
                scaleFactor = maxScale / origScale
            } else if (saveScale < minScale) {
                saveScale = minScale
                scaleFactor = minScale / origScale
            }

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
                imageMatrix2.postScale(scaleFactor, scaleFactor, viewWidth / 2f, viewHeight / 2f)
            } else {
                imageMatrix2.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            }
            fixTrans()
            return true
        }
    }

    private fun fixTrans() {
        imageMatrix2.getValues(m)
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]
        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), origWidth * saveScale)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), origHeight * saveScale)
        if (fixTransX != 0f || fixTransY != 0f) {
            imageMatrix2.postTranslate(fixTransX, fixTransY)
        }
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans) return -trans + minTrans
        if (trans > maxTrans) return -trans + maxTrans
        return 0f
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) 0f else delta
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        if ((oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight) ||
            viewWidth == 0 || viewHeight == 0
        ) return

        oldMeasuredHeight = viewHeight
        oldMeasuredWidth = viewWidth

        if (saveScale == 1f) {
            val drawable = drawable ?: return
            if (drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) return

            val bmWidth = drawable.intrinsicWidth
            val bmHeight = drawable.intrinsicHeight

            val scaleX = viewWidth.toFloat() / bmWidth.toFloat()
            val scaleY = viewHeight.toFloat() / bmHeight.toFloat()
            val scale = min(scaleX, scaleY)
            imageMatrix2.setScale(scale, scale)

            var redundantYSpace = viewHeight.toFloat() - scale * bmHeight.toFloat()
            var redundantXSpace = viewWidth.toFloat() - scale * bmWidth.toFloat()
            redundantYSpace /= 2f
            redundantXSpace /= 2f

            imageMatrix2.postTranslate(redundantXSpace, redundantYSpace)

            origWidth = viewWidth - 2 * redundantXSpace
            origHeight = viewHeight - 2 * redundantYSpace
            imageMatrix = imageMatrix2
        }
        fixTrans()
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
        private const val CLICK = 3
    }
}
