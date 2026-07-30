# Tech Events — API Mock Local

Esta pasta contém a **API Mock Local** desenvolvida em Node.js e Express para fornecer os dados remotos de eventos para o aplicativo Android **Tech Events**.

---

## 📌 Requisitos

- Node.js v18 ou superior instalado.
- npm ou yarn.

---

## 🚀 Instruções de Instalação e Execução

1. Navegue até a pasta `mock-api`:
   ```bash
   cd mock-api
   ```

2. Instale as dependências:
   ```bash
   npm install
   ```

3. Inicie o servidor:
   ```bash
   npm start
   ```

Por padrão, a API estará acessível em:
- **Localhost**: `http://localhost:3000`
- **Emulador Android**: `http://10.0.2.2:3000`
- **Dispositivo Físico**: `http://<SEU_IP_LOCAL>:3000`

---

## 🌐 Configuração da Base URL no App Android

A URL base da API é configurada de forma centralizada no arquivo `app/build.gradle.kts` via `BuildConfig.BASE_URL`:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000/\"")
    }
}
```

Para testar em um **dispositivo físico**, altere `10.0.2.2` para o IP da sua máquina na rede local (ex: `http://192.168.1.15:3000/`).

---

## 🔄 Restaurando os Dados Iniciais (Reset)

Caso adicione, edite ou exclua eventos durante o teste e deseje restaurar a base inicial de 30 eventos:

Execute uma chamada POST para `/reset`:

```bash
curl -X POST http://localhost:3000/reset
```

---

## 📑 Documentação dos Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| **GET** | `/events?offset=0&limit=10` | Retorna lista de eventos paginada (`offset` default: 0, `limit` default: 10) |
| **GET** | `/events/:id` | Retorna os detalhes de um evento pelo ID (Retorna HTTP 404 se não encontrado) |
| **POST** | `/events` | Cria um novo evento (Valida campos obrigatórios, retorna HTTP 201) |
| **PUT** | `/events/:id` | Atualiza um evento existente pelo ID |
| **DELETE** | `/events/:id` | Exclui um evento pelo ID do banco mock |
| **POST** | `/reset` | Restaura a base de dados para os 30 eventos fictícios padrão |

---

## 📝 Exemplo de Resposta JSON (`GET /events?offset=0&limit=1`)

```json
[
  {
    "id": 1,
    "title": "Android Dev Summit 2024 - São Paulo",
    "description": "Junte-se aos principais especialistas em Android do Brasil...",
    "category": "Android",
    "format": "PRESENCIAL",
    "date": "2024-03-15",
    "startTime": "19:00",
    "endTime": "22:00",
    "venueName": "Centro de Convenções Fiesp",
    "address": "Av. Paulista, 1578 - Bela Vista",
    "city": "São Paulo",
    "state": "SP",
    "organizer": "Google Developers Group SP",
    "imageUrl": "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
    "price": 0.0,
    "totalSeats": 150,
    "registeredParticipants": 87,
    "eventUrl": "https://gdg.community.dev/events/details/gdg-sp-android-dev-summit-2024",
    "latitude": -23.5614,
    "longitude": -46.6559
  }
]
```
