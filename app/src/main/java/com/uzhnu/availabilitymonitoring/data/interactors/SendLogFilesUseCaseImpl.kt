package com.uzhnu.availabilitymonitoring.data.interactors


import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.repository.ServerRepository
import com.uzhnu.availabilitymonitoring.domain.usecase.SendLogFilesUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendLogFilesUseCaseImpl @Inject constructor(
    private val applicationLogger: ApplicationLogger,
    private val serverRepository: ServerRepository
) : SendLogFilesUseCase {

    override suspend fun invoke(uuid: String): Result<Unit> =
        runCatching { applicationLogger.getAllLogFiles() }.fold(onSuccess = { files ->
            if (files == null) {
                Result.success(Unit)
            } else {
                uploadFiles(uuid, files)
            }
        }, onFailure = {
            FirebaseCrashlytics.getInstance().recordException(it)
            applicationLogger.log(TAG, MESSAGE_UPLOAD_FILE_FAILURE)
            Result.failure(it)
        })


    private suspend fun uploadFiles(
        uuid: String,
        files: List<File>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val runningTasks = files.map { file ->
            async { serverRepository.sendLog(uuid, file) }
        }
        val response = runningTasks.awaitAll()

        val result = response.find { it.isFailure } ?: Result.success(Unit)
        result.onSuccess { applicationLogger.log(TAG, MESSAGE_UPLOAD_FILE_SUCCESS) }
            .onFailure { applicationLogger.log(TAG, MESSAGE_UPLOAD_FILE_FAILURE) }

        return@withContext result
    }

    companion object {
        private const val TAG = "Send Logs"
        private const val MESSAGE_UPLOAD_FILE_SUCCESS = "file upload succeed"
        private const val MESSAGE_UPLOAD_FILE_FAILURE = "file upload failed"
    }

}
