package com.elssiany.klydo.worker

expect class BackgroundTaskManager {
    fun enqueueUniqueWork(
        taskId: String,
        constraints: WorkConstraints,
        task: suspend () -> Unit
    )

    fun enqueuePeriodicWork(
        taskId: String,
        interval: Long, // intervalo en milisegundos
        constraints: WorkConstraints,
        task: suspend () -> Unit
    )

    fun enqueueParallelTasks(
        taskId: String,
        tasks: List<suspend () -> Unit>,
        onComplete: () -> Unit
    )

    fun cancelTask(taskId: String)

    fun cancelAllTasks()
}