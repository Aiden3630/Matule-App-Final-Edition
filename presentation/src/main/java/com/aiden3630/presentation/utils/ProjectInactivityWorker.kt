package com.aiden3630.presentation.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ProjectInactivityWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Создаем сервис уведомлений
        val notificationService = NotificationService(
            applicationContext,
            com.aiden3630.data.manager.TokenManager(applicationContext)
        )

        notificationService.showNotification(
            title = "Проект заждался!",
            message = "Вы открыли проект и ничего не меняли уже 3 минуты. Нужно продолжить работу?"
        )

        return Result.success()
    }
}