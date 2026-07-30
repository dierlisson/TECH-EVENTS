package com.dierlisson.techevents.data.cache

import com.dierlisson.techevents.domain.model.Event
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache em memória thread-safe para busca e filtragem instantânea no aplicativo.
 */
class MemoryCache {
    private val cache = ConcurrentHashMap<Long, Event>()

    fun putAll(events: List<Event>) {
        events.forEach { cache[it.id] = it }
    }

    fun put(event: Event) {
        cache[event.id] = event
    }

    fun get(id: Long): Event? {
        return cache[id]
    }

    fun getAll(): List<Event> {
        return cache.values.toList()
    }

    fun remove(id: Long) {
        cache.remove(id)
    }

    fun clear() {
        cache.clear()
    }
}
