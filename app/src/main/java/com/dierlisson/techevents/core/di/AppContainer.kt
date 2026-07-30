package com.dierlisson.techevents.core.di

import android.content.Context

/**
 * Container de Injeção de Dependência Manual para a aplicação Tech Events.
 * Mantém e provê instâncias únicas dos repositórios, banco de dados local e serviço de API.
 */
class AppContainer(private val context: Context) {
    // Será estendido nas próximas fases com as instâncias do Retrofit, Room e Repository
}
