package com.liulkovich.news.domain.usecase

import com.liulkovich.news.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
){
    operator fun invoke() = settingsRepository.getSettings()
}