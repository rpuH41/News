package com.liulkovich.news.domain.usecase

import com.liulkovich.news.domain.entity.Language
import com.liulkovich.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
){
    suspend operator fun invoke(language: Language){
        settingsRepository.updateLanguage(language)
    }
}