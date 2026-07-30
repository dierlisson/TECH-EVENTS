package com.dierlisson.techevents.core.di

import android.content.Context
import com.dierlisson.techevents.BuildConfig
import com.dierlisson.techevents.data.cache.MemoryCache
import com.dierlisson.techevents.data.local.database.TechEventsDatabase
import com.dierlisson.techevents.data.remote.api.EventsApiService
import com.dierlisson.techevents.data.repository.EventsRepositoryImpl
import com.dierlisson.techevents.domain.repository.EventsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Container de Injeção de Dependência Manual para a aplicação Tech Events.
 * Mantém e provê instâncias únicas dos repositórios, banco de dados local e serviço de API.
 */
class AppContainer(private val context: Context) {

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val eventsApiService: EventsApiService by lazy {
        retrofit.create(EventsApiService::class.java)
    }

    val database: TechEventsDatabase by lazy {
        TechEventsDatabase.getInstance(context)
    }

    val memoryCache: MemoryCache by lazy {
        MemoryCache()
    }

    val eventsRepository: EventsRepository by lazy {
        EventsRepositoryImpl(
            apiService = eventsApiService,
            eventDao = database.eventDao(),
            favoriteDao = database.favoriteDao(),
            memoryCache = memoryCache
        )
    }
}
