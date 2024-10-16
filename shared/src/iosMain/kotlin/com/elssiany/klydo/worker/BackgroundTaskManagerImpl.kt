package com.elssiany.klydo.worker

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.*
import platform.BackgroundTasks.*
import platform.Foundation.*
import kotlinx.cinterop.*

actual class BackgroundTaskManager {
    private val taskOperationMap = mutableMapOf<String, NSOperationQueue>()

    actual fun enqueueUniqueWork(
        taskId: String,
        constraints: WorkConstraints,
        task: suspend () -> Unit
    ) {
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            taskId, usingQueue = null
        ) { bgTask ->
            GlobalScope.launch {
                if (constraints.meetsRequirements()) {
                    task()
                }
                bgTask?.setTaskCompletedWithSuccess(true)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun enqueuePeriodicWork(
        taskId: String,
        interval: Long,
        constraints: WorkConstraints,
        task: suspend () -> Unit
    ) {
        val request = BGProcessingTaskRequest(taskId)
        request.earliestBeginDate = NSDate().dateByAddingTimeInterval(interval / 1000.0)

        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            val success = BGTaskScheduler.sharedScheduler.submitTaskRequest(request, errorPtr.ptr)
            if (!success) {
                val error = errorPtr.value
                println("Error al programar la tarea: ${error?.localizedDescription ?: "Error desconocido"}")
            } else {
                println("Tarea programada correctamente.")
            }
        }
    }

    actual fun enqueueParallelTasks(
        taskId: String,
        tasks: List<suspend () -> Unit>,
        onComplete: () -> Unit
    ) {
        val operationQueue = NSOperationQueue()
        val coroutineScope = CoroutineScope(Dispatchers.Default)
        tasks.forEach { task ->
            operationQueue.addOperationWithBlock {
                coroutineScope.launch {
                    task()
                }
            }
        }
        operationQueue.addOperationWithBlock {
            onComplete()
        }
        taskOperationMap[taskId] = operationQueue
    }

    actual fun cancelTask(taskId: String) {
        val queue = taskOperationMap[taskId]
        if (queue != null) {
            queue.cancelAllOperations()
            taskOperationMap.remove(taskId)
        }
    }

    actual fun cancelAllTasks() {
        taskOperationMap.forEach { (_, operationQueue) ->
            operationQueue.cancelAllOperations()
        }
        taskOperationMap.clear()
    }
}