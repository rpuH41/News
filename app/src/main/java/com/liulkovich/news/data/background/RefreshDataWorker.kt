package com.liulkovich.news.data.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.liulkovich.news.domain.usecase.UpdateSubscribedArticlesUseCase

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val updateSubscribedArticlesUseCase: UpdateSubscribedArticlesUseCase
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("RefreshDataWorker", "Start")
        updateSubscribedArticlesUseCase()
        Log.d("RefreshDataWorker", "Finish")
        return Result.success()
    }
}