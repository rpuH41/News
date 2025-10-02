@file:OptIn(ExperimentalCoroutinesApi::class)

package com.liulkovich.news.presentation.screen.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.news.domain.entity.Article
import com.liulkovich.news.domain.usecase.AddSubscriptionUseCase
import com.liulkovich.news.domain.usecase.ClearAllArticlesUseCase
import com.liulkovich.news.domain.usecase.GetAllSubscriptionsUseCase
import com.liulkovich.news.domain.usecase.GetArticlesByTopicsUseCase
import com.liulkovich.news.domain.usecase.RemoveSubscriptionUseCase
import com.liulkovich.news.domain.usecase.UpdateSubscribedArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val removeSubscriptionUseCase: RemoveSubscriptionUseCase,
    private val updateSubscribedArticlesUseCase: UpdateSubscribedArticlesUseCase
): ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state = _state.asStateFlow()

    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    fun processCommand(command: SubscriptionsCommand) {
        when(command) {
            SubscriptionsCommand.ClearArticles -> {
                viewModelScope.launch {
                    val topics = state.value.selectedTopics
                    clearAllArticlesUseCase(topics)
                }
            }

            SubscriptionsCommand.ClickSubscribe -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        val topic = state.value.query.trim()
                        addSubscriptionUseCase(topic)
                        previousState.copy(query = "")
                    }
                }
            }

            is SubscriptionsCommand.InputTopic -> {
                _state.update { previousState ->
                    previousState.copy(query = command.query)
                }
            }

            SubscriptionsCommand.RefreshData -> {
                viewModelScope.launch {
                    updateSubscribedArticlesUseCase()
                }
            }

            is SubscriptionsCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    removeSubscriptionUseCase(command.topic)
                }

            }

            is SubscriptionsCommand.ToggleTopicSelection -> {
                _state.update { previousState ->
                    val subscriptions = previousState.subscriptions.toMutableMap()
                    val isSelected = subscriptions[command.topic] ?: false
                    subscriptions[command.topic] = !isSelected
                    previousState.copy(subscriptions = subscriptions)
                }
            }
        }
    }

    private fun observeSelectedTopics() {
        state.map { it.selectedTopics }
            .distinctUntilChanged()
            .flatMapMerge {
                getArticlesByTopicsUseCase(it)
            }
            .onEach {
                _state.update {previousState ->
                    previousState.copy(articles = it)

                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase()
            .onEach { subscriptions ->
                _state.update { previousState ->
                    val updateTopic = subscriptions.associateWith { topic ->
                        previousState.subscriptions[topic] ?: true
                    }
                    previousState.copy(subscriptions = updateTopic)
                }
            }.launchIn(viewModelScope)
    }
}

sealed interface SubscriptionsCommand{

    data class InputTopic(val query: String): SubscriptionsCommand

    data object ClickSubscribe: SubscriptionsCommand

    data object RefreshData: SubscriptionsCommand

    data class ToggleTopicSelection(val topic: String) : SubscriptionsCommand

    data object ClearArticles: SubscriptionsCommand

    data class RemoveSubscription(val topic: String): SubscriptionsCommand
}

data class SubscriptionsState(
    val query: String = "",
    val subscriptions: Map<String, Boolean> = mapOf(),
    val articles: List<Article> = listOf()
){
    val subscribeButtonEnabled: Boolean
        get() = query.isNotBlank()
    val selectedTopics: List<String>
        get() = subscriptions.filter{ it.value }.map { it.key }
}

