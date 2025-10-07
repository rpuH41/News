package com.liulkovich.news.data.repository

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.liulkovich.news.data.background.RefreshDataWorker
import com.liulkovich.news.data.local.ArticleDbModel
import com.liulkovich.news.data.local.NewsDao
import com.liulkovich.news.data.local.SubscriptionDbModel
import com.liulkovich.news.data.mapper.toDbModels
import com.liulkovich.news.data.mapper.toEntities
import com.liulkovich.news.data.remote.NewsApiService
import com.liulkovich.news.domain.entity.Article
import com.liulkovich.news.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService,
    private val workManager: WorkManager
): NewsRepository {

    init {
        startBackgroundRefresh()
    }

    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map{ subscriptions ->
            subscriptions.map{ it.topic}
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String) {
        val articles = loadArticles(topic)
        newsDao.addArticles(articles)
    }

    override suspend fun removeSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForAllSubscriptions() {
        val subscriptions = newsDao.getAllSubscriptions().first()
        coroutineScope {
            subscriptions.forEach {
                launch {
                    updateArticlesForTopic(it.topic)
                }
            }
        }
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            newsApiService.loadArticles(topic).toDbModels(topic)
        } catch (e: Exception) {
            if (e is CancellationException){
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            listOf()
        }

    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getAllArticlesByTopics(topics). map{
            it.toEntities()
        }
    }

    private fun startBackgroundRefresh() {
        val request = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            15L, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "Refresh data",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticleByTopics(topics)
    }
}