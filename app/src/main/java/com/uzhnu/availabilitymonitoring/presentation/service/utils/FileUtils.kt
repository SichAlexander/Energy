package com.uzhnu.availabilitymonitoring.presentation.service.utils

import android.annotation.SuppressLint
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

    private val TAG = FileUtils::class.simpleName
    private const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz"

    private fun prepareExternalFile(fileName: String, folder: File): File {
        folder.mkdirs()
        val file = File(folder, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
        return file
    }

    fun writeText(tag: String,text: String, fileName: String, folder: File) {
        prepareExternalFile(fileName, folder).appendText(
            StringBuilder().appendLine("${getFormattedCurrentTime()} -> $tag: $text").toString()
        )
    }

    private fun deleteFile(fileName: String, folder: File?) {
        folder?.mkdirs()
        val file = File(folder, fileName)
        if (file.exists()) file.delete()
    }

    fun deleteAllFilesExcept(
        exceptFileNames: List<String>,
        parentFolder: File?
    ) {
        parentFolder?.mkdirs()
        parentFolder?.list()?.filterNot { exceptFileNames.contains(it) }?.forEach { fileToDelete ->
            deleteFile(fileToDelete, parentFolder)
        }
    }

    fun File.getAllFilesInFolder(): List<File>? {
        return list()?.map { name ->
            File("$path/$name")
        }
    }

    @SuppressLint("SimpleDateFormat")
     fun getFormattedCurrentTime(): String{
        val simpleDateFormat = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        return simpleDateFormat.format(Date())
    }
}
