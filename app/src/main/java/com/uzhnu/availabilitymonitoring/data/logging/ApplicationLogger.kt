package com.uzhnu.availabilitymonitoring.data.logging

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment

import android.util.Log
import com.uzhnu.availabilitymonitoring.domain.model.RegionReport
import com.uzhnu.availabilitymonitoring.presentation.service.utils.FileUtils.deleteAllFilesExcept
import com.uzhnu.availabilitymonitoring.presentation.service.utils.FileUtils.getAllFilesInFolder
import com.uzhnu.availabilitymonitoring.presentation.service.utils.FileUtils.writeText
import com.uzhnu.availabilitymonitoring.presentation.service.utils.toAppStringFormat
import java.io.File
import java.io.FileOutputStream
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

    fun generatePdf(report: Map<String, RegionReport>, period: String): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 формат
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Малюємо заголовок
        val paint = android.graphics.Paint().apply {
            textSize = 18f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        canvas.drawText("Звіт про електроживлення", pageInfo.pageWidth / 2f, 50f, paint)
        canvas.drawText("Період: $period", pageInfo.pageWidth / 2f, 80f, paint)

        // Малюємо таблицю
        paint.textAlign = android.graphics.Paint.Align.LEFT
        paint.textSize = 14f

        val startX = 50f
        var startY = 120f

        canvas.drawText("Регіон", startX, startY, paint)
        canvas.drawText("Відсоток активності", startX + 200, startY, paint)
        canvas.drawText("Середній час відсутності (хв)", startX + 400, startY, paint)

        startY += 20f

        report.values.forEach { regionReport ->
            canvas.drawText(regionReport.regionId, startX, startY, paint)
            canvas.drawText("%.2f%%".format(regionReport.activePercentage), startX + 200, startY, paint)
            canvas.drawText("%.2f".format(regionReport.avgInactiveHours), startX + 400, startY, paint)
            startY += 20f
        }

        pdfDocument.finishPage(page)

        // Зберігаємо PDF у файл
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "PowerStatusReport_$period.pdf"
        )
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }

    /**
     *   fun generatePdf(report: Map<String, RegionReport>, period : String): File {
     *         val pdfDocument = PdfDocument()
     *         val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
     *         val page = pdfDocument.startPage(pageInfo)
     *         val canvas = page.canvas
     *
     *         // Текст налаштування
     *         val paint = android.graphics.Paint().apply {
     *             textSize = 14f
     *             textAlign = android.graphics.Paint.Align.LEFT
     *         }
     *
     *         var startY = 50f
     *         canvas.drawText("Звіт про електроживлення", 50f, startY, paint)
     *         startY += 20f
     *         canvas.drawText("Регіон        Відсоток активності        Середній час відсутності (год)", 50f, startY, paint)
     *         startY += 20f
     *
     *         report.values.forEach { regionReport ->
     *             val line = "${regionReport.regionId}        ${"%.2f".format(regionReport.activePercentage)}%        ${"%.2f".format(regionReport.avgInactiveHours)} год"
     *             canvas.drawText(line, 50f, startY, paint)
     *             startY += 20f
     *         }
     *
     *         pdfDocument.finishPage(page)
     *
     *         val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PowerStatusReport.pdf")
     *         pdfDocument.writeTo(FileOutputStream(file))
     *         pdfDocument.close()
     *
     *         return file
     *     }
     *
     */


    companion object {
        private const val LOG_FILE_NAME = "Log.txt"
        private const val LOG_FOLDER_NAME = "logs"
        private const val DAYS_KEEP_LOG_FILES = 4
    }
}
