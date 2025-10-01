package com.liulkovich.news.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.liulkovich.news.domain.repository.NewsRepository
import com.liulkovich.news.presentation.ui.theme.NewsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repository.addSubscription("Kotlin")
            repository.updateArticlesForTopic("Kotlin")
        }
        setContent {
            NewsTheme {

            }
        }
    }
}
