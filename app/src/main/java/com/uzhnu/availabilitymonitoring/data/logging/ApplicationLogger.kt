package com.uzhnu.availabilitymonitoring.data.logging

import android.content.Context

import android.util.Log
import com.uzhnu.availabilitymonitoring.presentation.service.utils.FileUtils.deleteAllFilesExcept
import com.uzhnu.availabilitymonitoring.presentation.service.utils.FileUtils.getAllFilesInFolder
import com.uzhnu.availabilitymonitoring.presentation.service.utils.FileUtils.writeText
import com.uzhnu.availabilitymonitoring.presentation.service.utils.toAppStringFormat
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationLogger @Inject constructor(private val context: Context?) {

    fun log(tag: String, message: String) {
        context?.let { writeText(tag, message, logFileName(), getFileDir()) }
        Log.i(tag, message)
    }

    private fun logFileName(): String {
        return getLogFileNamePerDate(Date())
    }

    private fun getLogFileNamePerDate(date: Date): String {
        return "${date.toAppStringFormat()}-$LOG_FILE_NAME"
    }

    private fun getFileDir(): File {
        val folder = File(context?.cacheDir, LOG_FOLDER_NAME)
        folder.mkdir()
        return folder
    }

    fun getAllLogFiles(): List<File>? {
        return getFileDir().getAllFilesInFolder()
    }

    fun cleanUpOldFiles() {
        val listOfDates = mutableListOf<Date>()
        for (i in 0 until DAYS_KEEP_LOG_FILES) {
            val date = Calendar.getInstance().also { it.add(Calendar.DATE, -i) }
            listOfDates.add(Date(date.timeInMillis))
        }

        deleteAllFilesExcept(
            exceptFileNames = listOfDates.map { getLogFileNamePerDate(it) },
            getFileDir()
        )
    }

    companion object {
        private const val LOG_FILE_NAME = "Log.txt"
        private const val LOG_FOLDER_NAME = "logs"
        private const val DAYS_KEEP_LOG_FILES = 4
    }
}
