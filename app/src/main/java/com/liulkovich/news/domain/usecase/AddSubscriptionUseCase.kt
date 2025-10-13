package com.liulkovich.news.domain.usecase

import com.liulkovich.news.domain.repository.NewsRepository
import com.liulkovich.news.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AddSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {

   suspend operator fun invoke(topic: String){
        newsRepository.addSubscription(topic)
       CoroutineScope(coroutineContext).launch {
           val settings = settingsRepository.getSettings().first()
           newsRepository.updateArticlesForTopic(topic, settings.language)
       }
    }
}