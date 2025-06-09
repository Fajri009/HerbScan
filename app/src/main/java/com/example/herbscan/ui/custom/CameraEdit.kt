package com.example.herbscan.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.herbscan.R

class CameraEdit @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): AppCompatEditText(context, attrs) {
    private var onClickListener: OnClickListener? = null
    private var cameraIcon: Drawable? = null

    init {
        // Disable text editing
        isFocusable = false
        isFocusableInTouchMode = false
        isClickable = true

        // Set gravity to center
        gravity = Gravity.CENTER

        // Load camera icon
        cameraIcon = ContextCompat.getDrawable(context, R.drawable.ic_camera)

        // Set padding
        setPaddingRelative(dpToPx(15), dpToPx(40), dpToPx(15), dpToPx(40))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw camera icon in center
        cameraIcon?.let { icon ->
            val iconSize = dpToPx(45)
            val iconLeft = (width - iconSize) / 2
            val iconTop = (height - iconSize) / 2

            icon.setBounds(
                iconLeft,
                iconTop,
                iconLeft + iconSize,
                iconTop + iconSize
            )
            icon.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            onClickListener?.onClick(this)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        onClickListener = listener
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}