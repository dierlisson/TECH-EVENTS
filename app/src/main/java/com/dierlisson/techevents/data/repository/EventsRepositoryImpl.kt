package com.dierlisson.techevents.data.repository

import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.data.cache.MemoryCache
import com.dierlisson.techevents.data.local.dao.EventDao
import com.dierlisson.techevents.data.local.dao.FavoriteDao
import com.dierlisson.techevents.data.local.entity.EventEntity
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
        val seedEntities = getInitialSeedEntities()
        val existingIds = localEntities.map { it.id }.toSet()
        val missingSeeds = seedEntities.filter { !existingIds.contains(it.id) }

        if (missingSeeds.isNotEmpty()) {
            eventDao.insertAll(seedEntities)
            localEntities = eventDao.getAllEvents()
        }

        val domainEvents = localEntities.map { entity ->
            entity.toDomain(isFavorite = favoriteIds.contains(entity.id))
        }
        memoryCache.putAll(domainEvents)
        return NetworkResult.Success(domainEvents)
    }

    private fun getInitialSeedEntities(): List<EventEntity> {
        return listOf(
            // Eventos Encerrados/Finalizados (Passados em relação a 30/07/2026)
            EventEntity(
                id = 101L,
                title = "[ENCERRADO] Android Dev Conference 2025",
                description = "Edição encerrada em 2025. Evento focado em migração para Jetpack Compose, gerenciamento de memória e boas práticas de publicação no Google Play.",
                category = "Android",
                format = "PRESENCIAL",
                date = "2025-10-15",
                startTime = "09:00",
                endTime = "18:00",
                venueName = "Centro de Convenções Rebouças",
                address = "Av. Rebouças, 600 - Pinheiros",
                city = "São Paulo",
                state = "SP",
                organizer = "Android Devs SP",
                imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
                price = 0.0,
                totalSeats = 200,
                registeredParticipants = 200,
                eventUrl = "https://developer.android.com",
                latitude = -23.5588,
                longitude = -46.6687
            ),
            EventEntity(
                id = 102L,
                title = "[ENCERRADO] Hackathon IA & Machine Learning 2025",
                description = "Edição encerrada em 2025. Desafio de 48 horas criando protótipos de Inteligência Artificial para solução de problemas urbanos e sociais.",
                category = "IA",
                format = "ONLINE",
                date = "2025-11-20",
                startTime = "18:00",
                endTime = "22:00",
                venueName = null,
                address = null,
                city = null,
                state = null,
                organizer = "AI Latam Group",
                imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=800",
                price = 0.0,
                totalSeats = 400,
                registeredParticipants = 400,
                eventUrl = "https://ailatam.org",
                latitude = null,
                longitude = null
            ),
            EventEntity(
                id = 103L,
                title = "[ENCERRADO] Web Performance & Frontend Day 2026",
                description = "Edição realizada no início de 2026 sobre otimização de Core Web Vitals, SSR com Next.js e acessibilidade web.",
                category = "Web",
                format = "PRESENCIAL",
                date = "2026-03-10",
                startTime = "10:00",
                endTime = "17:00",
                venueName = "Hub Carioca de Inovação",
                address = "Praça Mauá, 1 - Centro",
                city = "Rio de Janeiro",
                state = "RJ",
                organizer = "Frontend Masters RJ",
                imageUrl = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800",
                price = 49.90,
                totalSeats = 150,
                registeredParticipants = 150,
                eventUrl = "https://frontendmasters.br",
                latitude = -22.8961,
                longitude = -43.1812
            ),

            // Novos Eventos Futuros (Futuros em relação a 30/07/2026)
            EventEntity(
                id = 1L,
                title = "Android Dev Summit 2026 - São Paulo",
                description = "O maior evento focado em desenvolvimento Android da América Latina! Venha aprender sobre Jetpack, Kotlin Coroutines, Architecture Components, Performance e o futuro do ecossistema mobile com especialistas do mercado.",
                category = "Android",
                format = "PRESENCIAL",
                date = "2026-08-15",
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
            EventEntity(
                id = 2L,
                title = "Kotlin Multiplatform & AI Conference 2026",
                description = "Conferência 100% online explorando o uso de Kotlin Multiplatform (KMP) para compartilhamento de lógica entre Android e iOS, além da integração com Inteligência Artificial e Modelos LLM.",
                category = "Kotlin",
                format = "ONLINE",
                date = "2026-09-10",
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
            EventEntity(
                id = 3L,
                title = "Backend Clean Architecture Workshop",
                description = "Imersão prática em arquitetura limpa, microsserviços, desacoplamento de código, testes unitários de alta cobertura e boas práticas de integração contínua.",
                category = "Backend",
                format = "PRESENCIAL",
                date = "2026-10-05",
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
            ),
            EventEntity(
                id = 4L,
                title = "Cloud Native & Kubernetes Summit 2026",
                description = "Encontro presencial em Belo Horizonte focado em arquiteturas multicloud, gestão de clusters Kubernetes, Istio Service Mesh e automação com Terraform.",
                category = "Cloud",
                format = "PRESENCIAL",
                date = "2026-11-12",
                startTime = "09:00",
                endTime = "17:30",
                venueName = "BH TEC - Parque Tecnológico",
                address = "Rua Prof. José Vieira de Mendonça, 3011",
                city = "Belo Horizonte",
                state = "MG",
                organizer = "Cloud BH Community",
                imageUrl = "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800",
                price = 50.00,
                totalSeats = 120,
                registeredParticipants = 75,
                eventUrl = "https://cloudbh.org",
                latitude = -19.8692,
                longitude = -43.9664
            ),
            EventEntity(
                id = 5L,
                title = "DevOps Automation & CI/CD Day 2026",
                description = "Workshop online focado na construção de esteiras de CI/CD resilientes com GitHub Actions, ArgoCD e verificações de segurança em contêineres Docker.",
                category = "DevOps",
                format = "ONLINE",
                date = "2026-12-01",
                startTime = "10:00",
                endTime = "16:00",
                venueName = null,
                address = null,
                city = null,
                state = null,
                organizer = "DevOps Brasil Community",
                imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
                price = 0.0,
                totalSeats = 600,
                registeredParticipants = 410,
                eventUrl = "https://devopsbrasil.org",
                latitude = null,
                longitude = null
            ),
            EventEntity(
                id = 6L,
                title = "IA Generativa & Agentes Autônomos Summit 2027",
                description = "Desenvolvimento avançado de sistemas multi-agente, modelos LLM locais e integração com aplicações móveis.",
                category = "IA",
                format = "ONLINE",
                date = "2027-01-15",
                startTime = "19:00",
                endTime = "22:00",
                venueName = null,
                address = null,
                city = null,
                state = null,
                organizer = "AI South America",
                imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=800",
                price = 0.0,
                totalSeats = 1000,
                registeredParticipants = 650,
                eventUrl = "https://aisouthamerica.org",
                latitude = null,
                longitude = null
            ),
            EventEntity(
                id = 7L,
                title = "Modern Web & Performance Conference 2027",
                description = "Conferência presencial reunindo desenvolvedores frontend e engenheiros web para discutir o futuro dos browsers e otimização de performance.",
                category = "Web",
                format = "PRESENCIAL",
                date = "2027-02-20",
                startTime = "09:00",
                endTime = "18:00",
                venueName = "Lobo Coworking Curitiba",
                address = "Rua São Pedro, 460 - Cabral",
                city = "Curitiba",
                state = "PR",
                organizer = "Frontend Brasil",
                imageUrl = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800",
                price = 120.00,
                totalSeats = 180,
                registeredParticipants = 110,
                eventUrl = "https://frontendbrasil.dev",
                latitude = -25.4123,
                longitude = -49.2567
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
                val newId = System.currentTimeMillis()
                val createdEntity = event.copy(id = newId).toEntity()
                eventDao.insert(createdEntity)
                val domainEvent = createdEntity.toDomain(isFavorite = favoriteIds.contains(newId))
                memoryCache.put(domainEvent)
                NetworkResult.Success(domainEvent)
            }
        } catch (e: Exception) {
            val newId = System.currentTimeMillis()
            val createdEntity = event.copy(id = newId).toEntity()
            eventDao.insert(createdEntity)
            val domainEvent = createdEntity.toDomain(isFavorite = favoriteIds.contains(newId))
            memoryCache.put(domainEvent)
            NetworkResult.Success(domainEvent)
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
                val updatedEntity = event.toEntity()
                eventDao.insert(updatedEntity)
                val domainEvent = updatedEntity.toDomain(isFavorite = favoriteIds.contains(event.id))
                memoryCache.put(domainEvent)
                NetworkResult.Success(domainEvent)
            }
        } catch (e: Exception) {
            val updatedEntity = event.toEntity()
            eventDao.insert(updatedEntity)
            val domainEvent = updatedEntity.toDomain(isFavorite = favoriteIds.contains(event.id))
            memoryCache.put(domainEvent)
            NetworkResult.Success(domainEvent)
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
                eventDao.deleteById(id)
                memoryCache.remove(id)
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            eventDao.deleteById(id)
            memoryCache.remove(id)
            NetworkResult.Success(Unit)
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
