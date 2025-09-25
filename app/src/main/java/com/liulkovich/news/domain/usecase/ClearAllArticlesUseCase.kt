package com.liulkovich.news.domain.usecase

import com.liulkovich.news.domain.repository.NewsRepository
import javax.inject.Inject

class ClearAllArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

   suspend operator fun invoke(topic: List<String>){
        newsRepository.clearAllArticles(topic)
    }
}