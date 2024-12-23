package com.example.herbscan.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.herbscan.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {
    private var iconPassword: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_pass) as Drawable
    private var hidePassword : Drawable = ContextCompat.getDrawable(context, R.drawable.ic_close_pass) as Drawable
    private var showPassword: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_open_pass) as Drawable
    private var isPasswordVisible: Boolean = false

    init {
        setOnTouchListener(this)

        hidePassword()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8) {
                    setError(resources.getString(R.string.invalid_password), null)
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        maxLines = 1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setEditTextDrawables(startOfTheText = iconPassword,endOfTheText = if (isPasswordVisible) showPassword else hidePassword)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawables[2]
            if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width() - paddingEnd)) {
                isPasswordVisible = !isPasswordVisible
                if (!isPasswordVisible) {
                    hidePassword()
                } else {
                    showPassword()
                }

                setSelection(text?.length ?: 0)
                return true
            }
        }
        return false
    }

    private fun setEditTextDrawables(startOfTheText: Drawable? = null, topOfTheText: Drawable? = null, endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 20
    }

    private fun hidePassword() {
        val currentTypeface: Typeface? = typeface
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        typeface = currentTypeface
    }

    private fun showPassword() {
        val currentTypeface: Typeface? = typeface
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        typeface = currentTypeface
    }
}