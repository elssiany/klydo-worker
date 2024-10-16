package com.elssiany.klydo.worker

import android.content.Context
import androidx.work.*

actual class BackgroundTaskManager(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    actual fun enqueueUniqueWork(
        taskId: String,
        constraints: WorkConstraints,
        task: suspend () -> Unit
    ) {
        val workRequest = OneTimeWorkRequestBuilder<TaskWorker>()
            .setConstraints(toWorkConstraints(constraints))
            .build()

        workManager.enqueueUniqueWork(taskId, ExistingWorkPolicy.REPLACE, workRequest)
    }

    actual fun enqueuePeriodicWork(
        taskId: String,
        interval: Long,
        constraints: WorkConstraints,
        task: suspend () -> Unit
    ) {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<TaskWorker>(
            interval, java.util.concurrent.TimeUnit.MILLISECONDS
        )
            .setConstraints(toWorkConstraints(constraints))
            .build()

        workManager.enqueueUniquePeriodicWork(taskId, ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)
    }

    actual fun enqueueParallelTasks(
        taskId: String,
        tasks: List<suspend () -> Unit>,
        onComplete: () -> Unit
    ) {
        // Creamos una lista de `WorkRequest` para las tareas
        val workRequests = tasks.mapIndexed { index, _ ->
            val taskData = workDataOf("TASK_ID" to "$taskId-task-$index")

            OneTimeWorkRequestBuilder<TaskWorker>()
                .setInputData(taskData)
                .build()
        }

        // Encolar todas las tareas en paralelo
        val continuation = workManager.beginWith(workRequests)

        // Después de que todas las tareas terminen, ejecutar `onComplete`
        continuation.enqueue()
        continuation.then(
            OneTimeWorkRequestBuilder<OnCompleteWorker>()
                .build()
        ).enqueue()
    }

    actual fun cancelTask(taskId: String) {
        workManager.cancelUniqueWork(taskId)
    }

    actual fun cancelAllTasks() {
        workManager.cancelAllWork()
    }

    private fun toWorkConstraints(constraints: WorkConstraints): Constraints {
        val builder = Constraints.Builder()
        if (constraints.requiresNetwork) {
            builder.setRequiredNetworkType(NetworkType.CONNECTED)
        }
        if (constraints.requiresCharging) {
            builder.setRequiresCharging(true)
        }
        return builder.build()
    }
}