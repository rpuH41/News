package com.liulkovich.news.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.news.domain.entity.Interval
import com.liulkovich.news.domain.entity.Language
import com.liulkovich.news.domain.usecase.GetSettingsUseCase
import com.liulkovich.news.domain.usecase.UpdateIntervalUseCase
import com.liulkovich.news.domain.usecase.UpdateLanguageUseCase
import com.liulkovich.news.domain.usecase.UpdateNotificationsEnabledUseCase
import com.liulkovich.news.domain.usecase.UpdateWifiOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateIntervalUseCase: UpdateIntervalUseCase,
    private val updateWifiOnlyUseCase: UpdateWifiOnlyUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase
): ViewModel() {

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Initial)
    val state = _state.asStateFlow()

    init {
        getSettingsUseCase()
            .onEach { settings ->
                _state.update {
                    SettingsState.Configuration(
                        language = settings.language,
                        interval = settings.interval,
                        wifiOnly = settings.wifiOnly,
                        notificationsEnable = settings.notificationsEnabled
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    fun processCommand(command: SettingsCommand){
        viewModelScope.launch {
            when(command){
                is SettingsCommand.SelectedInterval -> {
                    updateIntervalUseCase(command.interval)
                }
                is SettingsCommand.SelectedLanguage -> {
                    updateLanguageUseCase(command.language)
                }
                is SettingsCommand.SetNotificationsEnabled -> {
                    updateNotificationsEnabledUseCase(command.enabled)
                }
                is SettingsCommand.SetWifiOnly -> {
                    updateWifiOnlyUseCase(command.wifiOnly)
                }
            }
        }
    }
}

sealed interface SettingsCommand {

    data class SelectedLanguage(val language: Language) : SettingsCommand

    data class SelectedInterval(val interval: Interval) : SettingsCommand

    data class SetNotificationsEnabled(val enabled: Boolean) : SettingsCommand

    data class SetWifiOnly(val wifiOnly: Boolean) : SettingsCommand

}


sealed interface SettingsState {

    data object Initial: SettingsState

    data class Configuration(
        val language: Language,
        val interval: Interval,
        val wifiOnly: Boolean,
        val notificationsEnable: Boolean,
        val languages: List<Language> = Language.entries,
        val intervals: List<Interval> = Interval.entries
    ): SettingsState
}