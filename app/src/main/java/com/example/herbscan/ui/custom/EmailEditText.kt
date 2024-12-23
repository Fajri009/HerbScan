package com.example.herbscan.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.herbscan.R
import com.example.herbscan.utils.Utils

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    private var iconEmail: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!Utils.isValidEmail(s)) {
                    setError(resources.getString(R.string.invalid_email), null)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        maxLines = 1
        isSingleLine = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setEditTextDrawables(startOfTheText = iconEmail)
    }

    private fun setEditTextDrawables(startOfTheText: Drawable? = null, topOfTheText: Drawable? = null, endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 20
    }
}