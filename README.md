# 📱 Tech Events — Aplicativo Android Nativo

> **Aplicativo nativo para Android em Kotlin projetado para portfólio profissional, focado na descoberta, filtragem, consulta de detalhes, gerenciamento completo (CRUD) e favoritos de eventos de tecnologia.**

![Android Native](https://img.shields.io/badge/Platform-Android_Native-green.svg)
![Language](https://img.shields.io/badge/Language-Kotlin_2.0-blue.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVVM_%2B_Repository-purple.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

---

## 🎯 Objetivo do Projeto

O **Tech Events** foi construído do zero com arquitetura limpa e escalável baseada em **MVVM + Repository Pattern**, priorizando boas práticas da engenharia Android moderna:
- **Layouts XML declarativos com DataBinding** (sem Jetpack Compose conforme especificado).
- **Estratégia Offline-First**: Cache persistente com **Room** e cache em memória thread-safe (`MemoryCache`).
- **Navegação com Single Activity**: Host central em `MainActivity` utilizando **Navigation Component** e **Safe Args**.
- **Injeção de Dependências Manual**: Gerenciada desacopladamente via `AppContainer` sem dependência de frameworks externos.
- **Suíte de Testes Automatizados**: Testes unitários de ViewModel, Repository, Mappers e Contrato de API com `MockWebServer`.
- **API Mock Local em Node.js/Express**: Incluída no próprio repositório com 30+ eventos determinísticos.

---

## 📸 Referência Visual & Interface do Usuário

O design visual reproduz com fidelidade a identidade do projeto:
- **Splash Screen**: Animação suave de entrada, logotipo vetorial original (`ic_tech_logo.xml`), gradientes em tons azul/roxo e subtítulo.
- **Lista de Eventos**: Campo de busca arredondado, chips horizontais para seleção de categorias (*Android, Kotlin, Backend, Web, IA, Cloud, DevOps*), chips exclusivos de formato (*Todos, Presencial, Online*), ordenação flexível (*Data mais próxima, Nome, Mais inscritos*), filtro de favoritos e cards com elevação sutil.
- **Detalhes do Evento**: Banner com gradiente fallback, card sobreposto com títulos e badges, seções estruturadas de informações e descrição, ações de favoritar, compartilhar nativo (`Intent.createChooser`), redirecionamento para Google Maps (`geo:`) e exclusão com diálogo de confirmação.
- **Formulário de Gerenciamento (CRUD)**: Formulário dinâmico reutilizável para criação e edição com seletores de data/hora (`DatePickerDialog`, `TimePickerDialog`), validação em tempo real e prevenção de alterações não salvas ao voltar.

---

## 📊 Diagrama do Fluxo de Dados (Mermaid)

```mermaid
flowchart TD
    subgraph UI ["Camada de Apresentação (Presentation)"]
        A[Fragment / Layout XML] -->|Observa LiveData / Envia Ações| B[ViewModel]
    end

    subgraph Domain ["Camada de Domínio (Domain)"]
        B -->|Depende de| C[EventsRepository Interface]
    end

    subgraph Data ["Camada de Dados (Data)"]
        C -->|Implementado por| D[EventsRepositoryImpl]
        D <-->|Leitura e Escrita Rápida| E[MemoryCache]
        D <-->|Persistência Local| F[Room Database: EventDao & FavoriteDao]
        D <-->|Requisições HTTP| G[Retrofit: EventsApiService]
    end

    G <-->|JSON REST| H[API Mock Local Node.js / Express]
```

---

## 🛠️ Stack Tecnológica

| Componente | Tecnologia |
| --- | --- |
| **Linguagem** | Kotlin 2.0.20 |
| **Arquitetura** | MVVM (Model-View-ViewModel) + Repository Pattern |
| **Injeção de Dependências** | Manual via `AppContainer` registrado na classe `Application` |
| **UI & Layouts** | XML, Material Components 3, DataBinding |
| **Navegação** | AndroidX Navigation Component com Safe Args |
| **Persistência Local** | Room Database (Entities, DAOs, Migrations) + MemoryCache |
| **Rede & HTTP** | Retrofit 2, OkHttp 4, Gson, HttpLoggingInterceptor |
| **Concorrência** | Kotlin Coroutines (`viewModelScope`, `Dispatchers.IO`) |
| **Splash Screen** | AndroidX Core SplashScreen (`core-splashscreen`) |
| **Carregamento de Imagens** | Glide 4 |
| **Testes Unitários** | JUnit 4, kotlinx-coroutines-test, Mockito, Mockito-Kotlin, Arch Core Testing, MockWebServer |
| **API Mock Local** | Node.js v22, Express, CORS |

---

## 📁 Estrutura do Projeto

```
com.dierlisson.techevents
├── core
│   ├── binding        # BindingAdapters para DataBinding
│   ├── di             # Injeção manual de dependências (AppContainer)
│   ├── network        # Sealed Interface NetworkResult e cliente HTTP
│   └── util           # Utilitários como NetworkUtils
├── data
│   ├── cache          # MemoryCache thread-safe
│   ├── local
│   │   ├── dao        # Room DAOs (EventDao, FavoriteDao)
│   │   ├── database   # TechEventsDatabase
│   │   └── entity     # Entidades Room (EventEntity, FavoriteEntity)
│   ├── mapper         # Mappers puros (EventMapper: DTO <-> Domain <-> Entity)
│   ├── remote
│   │   ├── api        # Retrofit EventsApiService
│   │   └── dto        # DTOs remotos (EventDto, CreateEventDto)
│   └── repository     # Implementação do repositório (EventsRepositoryImpl)
├── domain
│   ├── model          # Modelo de domínio puro (Event)
│   └── repository     # Interface desacoplada (EventsRepository)
└── presentation
    ├── adapter        # EventsAdapter (ListAdapter com DiffUtil e suporte a paginação)
    ├── events
    │   ├── detail     # EventDetailFragment, EventDetailViewModel, Factory
    │   ├── form       # EventFormFragment, EventFormViewModel, Factory
    │   └── list       # EventsListFragment, EventsListViewModel, Factory
    ├── main           # MainActivity (Host do NavHostFragment)
    ├── splash         # SplashFragment, SplashViewModel
    └── state          # Sealed interfaces de estado (UiState, EventsUiState, PaginationState)
```

---

## ⚙️ Instruções de Configuração e Execução

### 1. Clonar o Repositório

```bash
git clone https://github.com/dierlisson/TECH-EVENTS.git
cd TECH-EVENTS
```

### 2. Iniciar a API Mock Local

A API Mock é necessária para fornecer os endpoints remotos REST durante a execução do aplicativo.

```bash
cd mock-api
npm install
npm start
```
*A API iniciará no endereço `http://localhost:3000`.*

### 3. Configurar a Base URL do Aplicativo

A URL base da API é definida no arquivo `app/build.gradle.kts` via `BuildConfig.BASE_URL`:

- **Emulador Android Studio (Padrão)**: `http://10.0.2.2:3000/`
- **Dispositivo Físico**: Altere para o IP local do seu computador (ex: `http://192.168.1.15:3000/`).

### 4. Compilar e Executar o App Android

Abra o projeto no Android Studio ou compile pela linha de comando:

```bash
# Compilar projeto em modo Debug
.\gradlew.bat assembleDebug

# Executar a suíte de testes unitários
.\gradlew.bat testDebugUnitTest
```

---

## ⚡ Funcionalidades e Regras de Negócio

- **Paginação Infinita por Deslocamento**: Carregamento em blocos de 10 eventos (`offset=0&limit=10`). O rodapé exibe indicador de carregamento, mensagem de erro de paginação com botão "Tentar novamente" ou indicação de fim de lista.
- **Busca com Debounce**: Pesquisa por texto com **debounce de 400ms** cobrindo título, descrição, categoria, organizador, cidade e estado.
- **Filtros Combinados de Categoria & Formato**: Seleção exclusiva em chips de categorias e formato (*Presencial* vs *Online*), aplicados simultaneamente sem duplicar requisições.
- **Favoritos Persistentes (Offline)**: Favoritar e desfavoritar sincronicamente na lista e no detalhe, armazenados no Room localmente.
- **CRUD Completo de Eventos**:
  - **Criação (POST)**: Formulário com DatePicker, TimePicker, seletores de Categoria/Modalidade e validação de campos.
  - **Edição (PUT)**: Preenchimento automático dos campos existentes e atualização sincronizada no Room, cache e API.
  - **Exclusão (DELETE)**: Confirmação via diálogo e remoção remota/local.
- **Integrações Nativas**: Redirecionamento seguro para o Google Maps via Intent `geo:` (usando coordenadas ou endereço) e compartilhamento nativo de eventos.

---

## 👨‍💻 Autor e Licença

**Dierlisson Justiniano**
- GitHub: [@dierlisson](https://github.com/dierlisson)

Este projeto está sob a licença [MIT](LICENSE).
