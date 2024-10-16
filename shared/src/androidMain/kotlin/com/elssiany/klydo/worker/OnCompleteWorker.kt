package com.elssiany.klydo.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class OnCompleteWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Aquí llamamos a la función onComplete()
        println("All tasks completed")
        return Result.success()
    }
}