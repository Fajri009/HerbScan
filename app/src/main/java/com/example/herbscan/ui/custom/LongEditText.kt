package com.example.herbscan.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText

class LongEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
): AppCompatEditText(context, attrs) {
    init {
        maxLines = Int.MAX_VALUE
        isSingleLine = false
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE

        isVerticalScrollBarEnabled = true
        movementMethod = ScrollingMovementMethod()

        gravity = Gravity.TOP or Gravity.START
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setPaddingRelative(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(0))
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}