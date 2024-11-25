package com.uzhnu.availabilitymonitoring.presentation.service.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "dd.MM.yyyy"
private const val DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"

object DateUtils {

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val simpleDateFormat = SimpleDateFormat(DATE_TIME_FORMAT)
        return simpleDateFormat.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun Long.millisToDate(): String {
        val simpleDateFormat = SimpleDateFormat(DATE_TIME_FORMAT)
        return simpleDateFormat.format(Date(this))
    }
}

@SuppressLint("SimpleDateFormat")
fun Date.toAppStringFormat(): String {
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT)
    return simpleDateFormat.format(this)
}
