package com.liulkovich.news.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.liulkovich.news.data.local.NewsDao
import com.liulkovich.news.data.local.NewsDatabase
import com.liulkovich.news.data.remote.NewsApiService
import com.liulkovich.news.data.repository.NewsRepositoryImpl
import com.liulkovich.news.data.repository.SettingsRepositoryImpl
import com.liulkovich.news.domain.repository.NewsRepository
import com.liulkovich.news.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    companion object{

        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context
        ): WorkManager = WorkManager.getInstance(context)
        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(
            json: Json
        ): Converter.Factory {
            return json.asConverterFactory(
                "application/json".toMediaType()
            )
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(converterFactory)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(
            retrofit: Retrofit
        ): NewsApiService {
            return retrofit.create()
        }

        @Provides
        @Singleton
        fun provideNewsDatabase(
            @ApplicationContext context: Context
        ): NewsDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = NewsDatabase::class.java,
                name = "news.db"
            ).fallbackToDestructiveMigration().build()//не забыть удалить если публиковать приложение
        }

        @Provides
        @Singleton
        fun provideNewsDao(
            database: NewsDatabase
        ): NewsDao = database.newsDao()
    }
}