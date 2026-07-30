package com.dierlisson.techevents.data.repository

import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.data.cache.MemoryCache
import com.dierlisson.techevents.data.local.dao.EventDao
import com.dierlisson.techevents.data.local.dao.FavoriteDao
import com.dierlisson.techevents.data.local.entity.FavoriteEntity
import com.dierlisson.techevents.data.mapper.EventMapper.toCreateDto
import com.dierlisson.techevents.data.mapper.EventMapper.toDomain
import com.dierlisson.techevents.data.mapper.EventMapper.toEntity
import com.dierlisson.techevents.data.remote.api.EventsApiService
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.domain.repository.EventsRepository

class EventsRepositoryImpl(
    private val apiService: EventsApiService,
    private val eventDao: EventDao,
    private val favoriteDao: FavoriteDao,
    private val memoryCache: MemoryCache = MemoryCache()
) : EventsRepository {

    override suspend fun getEvents(offset: Int, limit: Int): NetworkResult<List<Event>> {
        val favoriteIds = favoriteDao.getFavoriteEventIds().toSet()

        return try {
            val response = apiService.getEvents(offset = offset, limit = limit)
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val entities = dtos.map { it.toEntity() }
                eventDao.insertAll(entities)

                val domainEvents = dtos.map { dto ->
                    dto.toDomain(isFavorite = favoriteIds.contains(dto.id))
                }
                memoryCache.putAll(domainEvents)

                NetworkResult.Success(domainEvents)
            } else {
                fetchFromLocalFallback(favoriteIds, response.code(), response.message())
            }
        } catch (e: Exception) {
            fetchFromLocalFallback(favoriteIds, -1, e.localizedMessage ?: "Erro de conexão", e)
        }
    }

    private suspend fun fetchFromLocalFallback(
        favoriteIds: Set<Long>,
        errorCode: Int,
        errorMessage: String,
        throwable: Throwable? = null
    ): NetworkResult<List<Event>> {
        var localEntities = eventDao.getAllEvents()
        if (localEntities.isEmpty()) {
            val seedEntities = getInitialSeedEntities()
            eventDao.insertAll(seedEntities)
            localEntities = eventDao.getAllEvents()
        }

        val domainEvents = localEntities.map { entity ->
            entity.toDomain(isFavorite = favoriteIds.contains(entity.id))
        }
        memoryCache.putAll(domainEvents)
        return NetworkResult.Success(domainEvents)
    }

    private fun getInitialSeedEntities(): List<com.dierlisson.techevents.data.local.entity.EventEntity> {
        return listOf(
            com.dierlisson.techevents.data.local.entity.EventEntity(
                id = 1L,
                title = "Android Dev Summit 2024 - São Paulo",
                description = "O maior evento focado em desenvolvimento Android da América Latina! Venha aprender sobre Jetpack, Kotlin Coroutines, Architecture Components, Performance e o futuro do ecossistema mobile com especialistas do mercado.",
                category = "Android",
                format = "PRESENCIAL",
                date = "2024-11-15",
                startTime = "09:00",
                endTime = "18:00",
                venueName = "Centro de Convenções Fiesp",
                address = "Av. Paulista, 1578 - Bela Vista",
                city = "São Paulo",
                state = "SP",
                organizer = "GDG São Paulo",
                imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
                price = 0.0,
                totalSeats = 250,
                registeredParticipants = 185,
                eventUrl = "https://developer.android.com",
                latitude = -23.5614,
                longitude = -46.6559
            ),
            com.dierlisson.techevents.data.local.entity.EventEntity(
                id = 2L,
                title = "Kotlin Multiplatform & AI Conference",
                description = "Conferência 100% online explorando o uso de Kotlin Multiplatform (KMP) para compartilhamento de lógica entre Android e iOS, além da integração com Inteligência Artificial e Modelos LLM.",
                category = "Kotlin",
                format = "ONLINE",
                date = "2024-11-20",
                startTime = "19:00",
                endTime = "22:00",
                venueName = null,
                address = null,
                city = null,
                state = null,
                organizer = "Kotlin Brasil",
                imageUrl = "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=800",
                price = 0.0,
                totalSeats = 500,
                registeredParticipants = 340,
                eventUrl = "https://kotlinlang.org",
                latitude = null,
                longitude = null
            ),
            com.dierlisson.techevents.data.local.entity.EventEntity(
                id = 3L,
                title = "Backend Clean Architecture Workshop",
                description = "Imersão prática em arquitetura limpa, microsserviços, desacoplamento de código, testes unitários de alta cobertura e boas práticas de integração contínua.",
                category = "Backend",
                format = "PRESENCIAL",
                date = "2024-12-05",
                startTime = "14:00",
                endTime = "19:00",
                venueName = "ACATE Tech Park",
                address = "Rod. SC-401, 4100 - Saco Grande",
                city = "Florianópolis",
                state = "SC",
                organizer = "DevsSC",
                imageUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800",
                price = 89.90,
                totalSeats = 80,
                registeredParticipants = 62,
                eventUrl = "https://acate.com.br",
                latitude = -27.5448,
                longitude = -48.4989
            )
        )
    }

    override suspend fun getEventById(id: Long): NetworkResult<Event> {
        val favoriteIds = favoriteDao.getFavoriteEventIds().toSet()

        // 1. Check Memory Cache
        val cached = memoryCache.get(id)
        if (cached != null) {
            return NetworkResult.Success(cached.copy(isFavorite = favoriteIds.contains(id)))
        }

        // 2. Try Remote API
        return try {
            val response = apiService.getEventById(id)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                eventDao.insert(dto.toEntity())
                val domainEvent = dto.toDomain(isFavorite = favoriteIds.contains(id))
                memoryCache.put(domainEvent)
                NetworkResult.Success(domainEvent)
            } else {
                // 3. Fallback to Local Room DB
                val localEntity = eventDao.getEventById(id)
                if (localEntity != null) {
                    val domainEvent = localEntity.toDomain(isFavorite = favoriteIds.contains(id))
                    memoryCache.put(domainEvent)
                    NetworkResult.Success(domainEvent)
                } else {
                    NetworkResult.Error(response.code(), response.message())
                }
            }
        } catch (e: Exception) {
            val localEntity = eventDao.getEventById(id)
            if (localEntity != null) {
                val domainEvent = localEntity.toDomain(isFavorite = favoriteIds.contains(id))
                memoryCache.put(domainEvent)
                NetworkResult.Success(domainEvent)
            } else {
                NetworkResult.Exception(e)
            }
        }
    }

    override suspend fun createEvent(event: Event): NetworkResult<Event> {
        val favoriteIds = favoriteDao.getFavoriteEventIds().toSet()
        return try {
            val response = apiService.createEvent(event.toCreateDto())
            if (response.isSuccessful && response.body() != null) {
                val createdDto = response.body()!!
                eventDao.insert(createdDto.toEntity())
                val domainEvent = createdDto.toDomain(isFavorite = favoriteIds.contains(createdDto.id))
                memoryCache.put(domainEvent)
                NetworkResult.Success(domainEvent)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun updateEvent(event: Event): NetworkResult<Event> {
        val favoriteIds = favoriteDao.getFavoriteEventIds().toSet()
        return try {
            val response = apiService.updateEvent(event.id, event.toCreateDto())
            if (response.isSuccessful && response.body() != null) {
                val updatedDto = response.body()!!
                eventDao.insert(updatedDto.toEntity())
                val domainEvent = updatedDto.toDomain(isFavorite = favoriteIds.contains(updatedDto.id))
                memoryCache.put(domainEvent)
                NetworkResult.Success(domainEvent)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun deleteEvent(id: Long): NetworkResult<Unit> {
        return try {
            val response = apiService.deleteEvent(id)
            if (response.isSuccessful) {
                eventDao.deleteById(id)
                memoryCache.remove(id)
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun toggleFavorite(eventId: Long): Boolean {
        val isFav = favoriteDao.isFavorite(eventId)
        if (isFav) {
            favoriteDao.deleteByEventId(eventId)
        } else {
            favoriteDao.insert(FavoriteEntity(eventId = eventId))
        }
        val newFavState = !isFav

        // Update memory cache
        val cached = memoryCache.get(eventId)
        if (cached != null) {
            memoryCache.put(cached.copy(isFavorite = newFavState))
        }

        return newFavState
    }

    override suspend fun getFavoriteEventIds(): Set<Long> {
        return favoriteDao.getFavoriteEventIds().toSet()
    }

    override fun getCachedEvents(): List<Event> {
        return memoryCache.getAll()
    }
}
