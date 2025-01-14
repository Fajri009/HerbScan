package com.example.herbscan.utils

import android.content.Context
import android.util.Log
import android.util.Patterns
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private val historyChatTimeFormat = "dd MMMM yyyy (HH:mm)"

    fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat(historyChatTimeFormat, Locale.getDefault()).format(currentTime)
    }
}