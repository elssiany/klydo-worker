package com.elssiany.klydo.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class TaskWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString("TASK_ID") ?: return Result.failure()
        try {
            // Aquí puedes ejecutar tu lógica de tarea suspendida
            println("Task $taskId started")
            delay(2000) // Simula una tarea de 2 segundos
            println("Task $taskId completed")
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}