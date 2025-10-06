package com.liulkovich.news.domain.usecase

import com.liulkovich.news.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AddSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

   suspend operator fun invoke(topic: String){
        newsRepository.addSubscription(topic)
       CoroutineScope(coroutineContext).launch {
           newsRepository.updateArticlesForTopic(topic)
       }
    }
}