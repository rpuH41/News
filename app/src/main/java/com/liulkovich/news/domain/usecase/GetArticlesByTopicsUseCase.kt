package com.liulkovich.news.domain.usecase

import com.liulkovich.news.domain.entity.Article
import com.liulkovich.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticlesByTopicsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

   operator fun invoke(topic: List<String>): Flow<List<Article>> {
        return newsRepository.getArticlesByTopics(topic)
    }
}