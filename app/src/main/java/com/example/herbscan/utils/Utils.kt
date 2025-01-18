package com.example.herbscan.utils

import android.content.Context
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
    private const val historyChatTimeFormat = "dd MMMM yyyy (HH:mm)"

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

    fun getRelativeTimeDifference(time: String): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy (HH:mm)", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()

        return try {
            val chatDate = formatter.parse(time)!!
            val now = Calendar.getInstance().time
            val diff = now.time - chatDate.time // Selisih dalam milidetik

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                days > 0 -> "$days hari lalu"
                hours > 0 -> "$hours jam lalu"
                minutes > 0 -> "$minutes menit lalu"
                else -> "Baru saja"
            }
        } catch (e: ParseException) {
            "Waktu tidak valid"
        }
    }
}