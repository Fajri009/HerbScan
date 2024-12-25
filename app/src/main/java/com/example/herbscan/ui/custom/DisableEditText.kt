package com.example.herbscan.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.herbscan.R

class DisableEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): AppCompatEditText(context, attrs) {
    private var iconEmail: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable

    init {
        isEnabled = false
        isFocusable = false
        isFocusableInTouchMode = false

        inputType = InputType.TYPE_NULL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setEdiTextDrawables(startOfTheText = iconEmail)
        setPaddingRelative(dpToPx(15), dpToPx(0), dpToPx(15), dpToPx(0))
    }

    private fun setEdiTextDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 20
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}