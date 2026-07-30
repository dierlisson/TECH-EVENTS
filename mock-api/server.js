const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const initialEvents = [
  {
    id: 1,
    title: "Android Dev Summit 2024 - São Paulo",
    description: "Junte-se aos principais especialistas em Android do Brasil para palestras profundas sobre Jetpack, arquitetura MVVM, Kotlin Coroutines, otimização de performance e novidades da plataforma.",
    category: "Android",
    format: "PRESENCIAL",
    date: "2024-03-15",
    startTime: "19:00",
    endTime: "22:00",
    venueName: "Centro de Convenções Fiesp",
    address: "Av. Paulista, 1578 - Bela Vista",
    city: "São Paulo",
    state: "SP",
    organizer: "Google Developers Group SP",
    imageUrl: "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
    price: 0.0,
    totalSeats: 150,
    registeredParticipants: 87,
    eventUrl: "https://gdg.community.dev/events/details/gdg-sp-android-dev-summit-2024",
    latitude: -23.5614,
    longitude: -46.6559
  },
  {
    id: 2,
    title: "Kotlin Multiplatform: Build Once, Run Everywhere",
    description: "Aprenda como compartilhar código de negócio entre Android, iOS e Backend utilizando Kotlin Multiplatform (KMP) com exemplos práticos e arquitetura limpa.",
    category: "Kotlin",
    format: "ONLINE",
    date: "2024-03-20",
    startTime: "19:30",
    endTime: "21:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Kotlin Brasil",
    imageUrl: "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800",
    price: 0.0,
    totalSeats: 500,
    registeredParticipants: 342,
    eventUrl: "https://youtube.com/live/kotlin-multiplatform-2024",
    latitude: null,
    longitude: null
  },
  {
    id: 3,
    title: "Backend Summit: Arquitetura de Microserviços de Alta Escala",
    description: "Imersão completa em padrões de microsserviços, resiliência com Resilience4j, comunicação assíncrona com Kafka e mensageria distribuída.",
    category: "Backend",
    format: "PRESENCIAL",
    date: "2024-03-25",
    startTime: "09:00",
    endTime: "18:00",
    venueName: "EXPO Center Norte",
    address: "Rua José Bernardo Pinto, 333 - Vila Guilherme",
    city: "São Paulo",
    state: "SP",
    organizer: "Backend Engineers Community",
    imageUrl: "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800",
    price: 149.90,
    totalSeats: 200,
    registeredParticipants: 180,
    eventUrl: "https://backendsummit.com.br",
    latitude: -23.5074,
    longitude: -46.6186
  },
  {
    id: 4,
    title: "IA Generativa na Prática com Gemini API",
    description: "Workshop hands-on sobre integração de modelos de Inteligência Artificial Generativa em aplicações web e mobile utilizando o Google Gemini SDK.",
    category: "IA",
    format: "ONLINE",
    date: "2024-04-02",
    startTime: "20:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "AI Latam Group",
    imageUrl: "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=800",
    price: 0.0,
    totalSeats: 1000,
    registeredParticipants: 890,
    eventUrl: "https://meet.google.com/gemini-ai-workshop",
    latitude: null,
    longitude: null
  },
  {
    id: 5,
    title: "DevOps & Cloud Native Day Rio de Janeiro",
    description: "Evento dedicado a Kubernetes, Terraform, CI/CD pipelines, GitOps com ArgoCD e segurança em ambientes de nuvem pública AWS e GCP.",
    category: "DevOps",
    format: "PRESENCIAL",
    date: "2024-04-10",
    startTime: "08:30",
    endTime: "17:30",
    venueName: "Hub de Inovação Carioca",
    address: "Praça Mauá, 1 - Centro",
    city: "Rio de Janeiro",
    state: "RJ",
    organizer: "DevOps Rio",
    imageUrl: "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
    price: 89.00,
    totalSeats: 120,
    registeredParticipants: 95,
    eventUrl: "https://devopsrio.com.br",
    latitude: -22.8961,
    longitude: -43.1812
  },
  {
    id: 6,
    title: "Web Performance & Modern Frontend",
    description: "Descubra técnicas avançadas para otimizar Core Web Vitals, renderização SSR/SSG, carregamento progressivo e acessibilidade em aplicações Web de grande porte.",
    category: "Web",
    format: "ONLINE",
    date: "2024-04-15",
    startTime: "19:00",
    endTime: "21:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Frontend Masters BR",
    imageUrl: "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800",
    price: 0.0,
    totalSeats: 400,
    registeredParticipants: 215,
    eventUrl: "https://frontendmasters.br/web-perf",
    latitude: null,
    longitude: null
  },
  {
    id: 7,
    title: "Cloud Summit BH: Serverless & Kubernetes",
    description: "Encontro técnico em Belo Horizonte reunindo arquitetos de software e engenheiros de infraestrutura para discutir estratégias serverless e arquitetura multicloud.",
    category: "Cloud",
    format: "PRESENCIAL",
    date: "2024-04-22",
    startTime: "14:00",
    endTime: "20:00",
    venueName: "BH TEC - Parque Tecnológico",
    address: "Rua Professor José Vieira de Mendonça, 3011 - Engenho Nogueira",
    city: "Belo Horizonte",
    state: "MG",
    organizer: "Cloud BH Community",
    imageUrl: "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800",
    price: 50.00,
    totalSeats: 100,
    registeredParticipants: 78,
    eventUrl: "https://cloudbh.org/summit2024",
    latitude: -19.8692,
    longitude: -43.9664
  },
  {
    id: 8,
    title: "Android Architecture Deep Dive: Clean & MVVM",
    description: "Masterclass focado em modularização de aplicativos Android, isolamento de camadas domain/data/presentation e testes unitários eficazes.",
    category: "Android",
    format: "ONLINE",
    date: "2024-04-28",
    startTime: "10:00",
    endTime: "13:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Android Devs Brasil",
    imageUrl: "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=800",
    price: 0.0,
    totalSeats: 300,
    registeredParticipants: 290,
    eventUrl: "https://androiddevs.com.br/architecture-masterclass",
    latitude: null,
    longitude: null
  },
  {
    id: 9,
    title: "Kotlin Coroutines & Flow em Produção",
    description: "Aprenda a tratar estados reativos, concorrência segura, tratamento estruturado de exceções e testes assíncronos com kotlinx-coroutines-test.",
    category: "Kotlin",
    format: "ONLINE",
    date: "2024-05-05",
    startTime: "19:00",
    endTime: "21:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Kotlin South America",
    imageUrl: "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800",
    price: 0.0,
    totalSeats: 600,
    registeredParticipants: 512,
    eventUrl: "https://kotlin.link/coroutines-flow",
    latitude: null,
    longitude: null
  },
  {
    id: 10,
    title: "Floripa Tech Weekend: Inovação e Soluções Web",
    description: "O maior encontro de tecnologia de Florianópolis reunindo desenvolvedores fullstack, designers de produto e lideranças de startups.",
    category: "Web",
    format: "PRESENCIAL",
    date: "2024-05-12",
    startTime: "09:00",
    endTime: "19:00",
    venueName: "ACATE Primavera Tech Park",
    address: "Rodovia SC-401, 4190 - Saco Grande",
    city: "Florianópolis",
    state: "SC",
    organizer: "ACATE Tech",
    imageUrl: "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800",
    price: 120.00,
    totalSeats: 250,
    registeredParticipants: 210,
    eventUrl: "https://acate.com.br/floripatech2024",
    latitude: -27.5458,
    longitude: -48.4989
  },
  {
    id: 11,
    title: "Segurança Avançada em APIs REST com Spring Security & OAuth2",
    description: "Boas práticas de segurança em APIs, prevenção de vulnerabilidades OWASP Top 10, autenticação JWT e autorização baseada em escopos.",
    category: "Backend",
    format: "ONLINE",
    date: "2024-05-18",
    startTime: "14:00",
    endTime: "18:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Java & Spring Brasil",
    imageUrl: "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800",
    price: 79.90,
    totalSeats: 150,
    registeredParticipants: 115,
    eventUrl: "https://springsecurity.com.br/workshop",
    latitude: null,
    longitude: null
  },
  {
    id: 12,
    title: "Machine Learning Ops (MLOps): Da Teoria ao Deploy",
    description: "Como estruturar pipelines automatizados para treinar, validar, versionar e monitorar modelos de Machine Learning em produção.",
    category: "IA",
    format: "PRESENCIAL",
    date: "2024-05-24",
    startTime: "13:30",
    endTime: "18:30",
    venueName: "Porto Digital Recife",
    address: "Cais do Apolo, 222 - Bairro do Recife",
    city: "Recife",
    state: "PE",
    organizer: "Data Science Pernambuco",
    imageUrl: "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800",
    price: 60.00,
    totalSeats: 80,
    registeredParticipants: 74,
    eventUrl: "https://portodigital.org/mlops-recife",
    latitude: -8.0631,
    longitude: -34.8711
  },
  {
    id: 13,
    title: "Observabilidade em Sistemas Distribuídos com OpenTelemetry",
    description: "Implementando tráfego rastreado, métricas distribuídas e logs estruturados com Jaeger, Prometheus e Grafana.",
    category: "DevOps",
    format: "ONLINE",
    date: "2024-06-01",
    startTime: "19:00",
    endTime: "21:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "SRE South America",
    imageUrl: "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800",
    price: 0.0,
    totalSeats: 350,
    registeredParticipants: 280,
    eventUrl: "https://sre.community/opentelemetry",
    latitude: null,
    longitude: null
  },
  {
    id: 14,
    title: "Android Performance: Memory Leaks & Profiling",
    description: "Aprenda a usar o Android Studio Profiler, LeakCanary e R8/ProGuard para diagnosticar gargalos de memória e otimizar a inicialização do app.",
    category: "Android",
    format: "PRESENCIAL",
    date: "2024-06-08",
    startTime: "09:30",
    endTime: "13:00",
    venueName: "Tecnopuc Porto Alegre",
    address: "Av. Ipiranga, 6681 - Partenon",
    city: "Porto Alegre",
    state: "RS",
    organizer: "GDG Porto Alegre",
    imageUrl: "https://images.unsplash.com/photo-1526498460520-4c246339dccb?w=800",
    price: 0.0,
    totalSeats: 90,
    registeredParticipants: 65,
    eventUrl: "https://gdgpoa.dev/android-profiling",
    latitude: -30.0617,
    longitude: -51.1731
  },
  {
    id: 15,
    title: "Google Cloud Platform for Developers",
    description: "Treinamento intensivo cobrindo Cloud Run, Firestore, Pub/Sub e Cloud Functions para desenvolvedores modernos.",
    category: "Cloud",
    format: "ONLINE",
    date: "2024-06-15",
    startTime: "18:30",
    endTime: "21:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "GCP Community Brasil",
    imageUrl: "https://images.unsplash.com/photo-1484417894907-623942c8ee29?w=800",
    price: 0.0,
    totalSeats: 800,
    registeredParticipants: 740,
    eventUrl: "https://gcp.community/dev-day",
    latitude: null,
    longitude: null
  },
  {
    id: 16,
    title: "Kotlin DSL & Gradle Build Optimization",
    description: "Melhore o tempo de build do seu projeto Android criando plugins Gradle customizados com Kotlin DSL e gerenciando dependências via Version Catalogs.",
    category: "Kotlin",
    format: "ONLINE",
    date: "2024-06-22",
    startTime: "20:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Kotlin Devs BR",
    imageUrl: "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800",
    price: 0.0,
    totalSeats: 300,
    registeredParticipants: 195,
    eventUrl: "https://kotlindevs.br/gradle-dsl",
    latitude: null,
    longitude: null
  },
  {
    id: 17,
    title: "Curitiba Web & React Meetup",
    description: "Palestras focadas em Server Components, Next.js App Router, estado global e design systems escaláveis.",
    category: "Web",
    format: "PRESENCIAL",
    date: "2024-06-28",
    startTime: "19:00",
    endTime: "22:00",
    venueName: "Lobo Coworking Curitiba",
    address: "Rua São Pedro, 460 - Cabral",
    city: "Curitiba",
    state: "PR",
    organizer: "Frontend Curitiba",
    imageUrl: "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800",
    price: 25.00,
    totalSeats: 70,
    registeredParticipants: 58,
    eventUrl: "https://curitibaweb.dev/meetup-junho",
    latitude: -25.4123,
    longitude: -49.2567
  },
  {
    id: 18,
    title: "Engenharia de Dados e Data Warehousing em Nuvem",
    description: "Pipelines de dados escaláveis com Apache Spark, BigQuery e Databricks para análise de dados em tempo real.",
    category: "Backend",
    format: "ONLINE",
    date: "2024-07-05",
    startTime: "19:30",
    endTime: "21:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Data Engineering Brasil",
    imageUrl: "https://images.unsplash.com/photo-1504868584819-f8e8b4b6d7e3?w=800",
    price: 0.0,
    totalSeats: 500,
    registeredParticipants: 410,
    eventUrl: "https://dataengineering.br/dw-cloud",
    latitude: null,
    longitude: null
  },
  {
    id: 19,
    title: "IA para Desenvolvedores: Visão Computacional e NLP",
    description: "Construa protótipos de reconhecimento de imagens e processamento de linguagem natural utilizando Python, PyTorch e Hugging Face.",
    category: "IA",
    format: "PRESENCIAL",
    date: "2024-07-12",
    startTime: "09:00",
    endTime: "17:00",
    venueName: "Impacta Tecnologia SP",
    address: "Av. Rudge, 315 - Barra Funda",
    city: "São Paulo",
    state: "SP",
    organizer: "Deep Learning Brasil",
    imageUrl: "https://images.unsplash.com/photo-1507146426996-ef05306b995a?w=800",
    price: 199.00,
    totalSeats: 100,
    registeredParticipants: 82,
    eventUrl: "https://deeplearning.br/ia-dev-sp",
    latitude: -23.5245,
    longitude: -46.6512
  },
  {
    id: 20,
    title: "Infraestrutura como Código (IaC) com Terraform e Pulumi",
    description: "Gerenciamento declarativo de infraestrutura, controle de estado, automação de ambientes e testes de infraestrutura.",
    category: "DevOps",
    format: "ONLINE",
    date: "2024-07-19",
    startTime: "19:00",
    endTime: "21:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Infrastructure BR",
    imageUrl: "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800",
    price: 0.0,
    totalSeats: 400,
    registeredParticipants: 320,
    eventUrl: "https://iac.community.br/terraform-pulumi",
    latitude: null,
    longitude: null
  },
  {
    id: 21,
    title: "Android Testing: Unit, Integration & UI with Espresso",
    description: "Aumente a confiabilidade do seu código aprendendo a escrever testes unitários rápidos em JVM, MockWebServer e testes automatizados de UI.",
    category: "Android",
    format: "ONLINE",
    date: "2024-07-26",
    startTime: "20:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Android Quality Guild",
    imageUrl: "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800",
    price: 0.0,
    totalSeats: 350,
    registeredParticipants: 285,
    eventUrl: "https://androidquality.org/testing-mastery",
    latitude: null,
    longitude: null
  },
  {
    id: 22,
    title: "Campinas Tech Day: Inovação Cloud & Mobile",
    description: "Encontro regional em Campinas para bate-papo técnico sobre modernização de legado, nuvem híbrida e arquitetura mobile.",
    category: "Cloud",
    format: "PRESENCIAL",
    date: "2024-08-02",
    startTime: "10:00",
    endTime: "16:00",
    venueName: "HUB Campinas",
    address: "Av. José de Souza Campos, 900 - Cambuí",
    city: "Campinas",
    state: "SP",
    organizer: "Campinas Tech Community",
    imageUrl: "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800",
    price: 30.00,
    totalSeats: 110,
    registeredParticipants: 90,
    eventUrl: "https://campinastech.org/techday2024",
    latitude: -22.8983,
    longitude: -47.0541
  },
  {
    id: 23,
    title: "Kotlin & Spring Boot 3: Construindo APIs Reativas",
    description: "Desenvolva APIs de alta performance com Spring WebFlux, R2DBC e Kotlin Coroutines integradas ao ecossistema Java moderno.",
    category: "Kotlin",
    format: "PRESENCIAL",
    date: "2024-08-09",
    startTime: "19:00",
    endTime: "22:00",
    venueName: "Impact Hub Brasília",
    address: "SGAN 601 Lote H - Asa Norte",
    city: "Brasília",
    state: "DF",
    organizer: "Kotlin Brasília",
    imageUrl: "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800",
    price: 40.00,
    totalSeats: 80,
    registeredParticipants: 62,
    eventUrl: "https://kotlinbsb.dev/spring-boot-reactive",
    latitude: -15.7801,
    longitude: -47.8825
  },
  {
    id: 24,
    title: "Design Systems Escaláveis com HTML/CSS Moderno",
    description: "Criação de componentes acessíveis, arquitetura CSS modular, Design Tokens, variáveis CSS e suporte a modo escuro nativo.",
    category: "Web",
    format: "ONLINE",
    date: "2024-08-16",
    startTime: "19:30",
    endTime: "21:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "UI/UX & Web Brasil",
    imageUrl: "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?w=800",
    price: 0.0,
    totalSeats: 450,
    registeredParticipants: 398,
    eventUrl: "https://designsystems.br/web-workshop",
    latitude: null,
    longitude: null
  },
  {
    id: 25,
    title: "GraphQL vs REST: Escolhendo a Arquitetura Certa",
    description: "Comparativo técnico detalhado sobre sobrecarga de rede, suporte a cache, versionamento e facilidade de integração em clientes mobile e web.",
    category: "Backend",
    format: "ONLINE",
    date: "2024-08-22",
    startTime: "20:00",
    endTime: "21:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "API Architecture Group",
    imageUrl: "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800",
    price: 0.0,
    totalSeats: 600,
    registeredParticipants: 480,
    eventUrl: "https://apiarchitecture.dev/graphql-vs-rest",
    latitude: null,
    longitude: null
  },
  {
    id: 26,
    title: "Agentes Autônomos de IA & RAG (Retrieval-Augmented Generation)",
    description: "Desenvolvimento de agentes inteligentes com acesso a dados corporativos em tempo real utilizando LangChain e Vector Databases.",
    category: "IA",
    format: "ONLINE",
    date: "2024-08-29",
    startTime: "19:00",
    endTime: "21:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "AI Engineers Community",
    imageUrl: "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800",
    price: 0.0,
    totalSeats: 1200,
    registeredParticipants: 1050,
    eventUrl: "https://aiengineers.dev/rag-agents",
    latitude: null,
    longitude: null
  },
  {
    id: 27,
    title: "Kubernetes Security & Hardening Masterclass",
    description: "Segurança de clusters, políticas de acesso RBAC, isolamento de pods com NetworkPolicies e varredura de contêineres em tempo de execução.",
    category: "DevOps",
    format: "PRESENCIAL",
    date: "2024-09-05",
    startTime: "09:00",
    endTime: "17:00",
    venueName: "WeWork Avenida Paulista",
    address: "Av. Paulista, 1374 - Bela Vista",
    city: "São Paulo",
    state: "SP",
    organizer: "K8s Security Brasil",
    imageUrl: "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800",
    price: 249.00,
    totalSeats: 60,
    registeredParticipants: 45,
    eventUrl: "https://k8ssecurity.br/hardening",
    latitude: -23.5629,
    longitude: -46.6542
  },
  {
    id: 28,
    title: "Android Background Work com WorkManager",
    description: "Aprenda a agendar tarefas diferidas em segundo plano garantindo execução persistente mesmo após a reinicialização do sistema operacional.",
    category: "Android",
    format: "ONLINE",
    date: "2024-09-12",
    startTime: "19:30",
    endTime: "21:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Android Devs Brasil",
    imageUrl: "https://images.unsplash.com/photo-1526498460520-4c246339dccb?w=800",
    price: 0.0,
    totalSeats: 400,
    registeredParticipants: 310,
    eventUrl: "https://androiddevs.com.br/workmanager-workshop",
    latitude: null,
    longitude: null
  },
  {
    id: 29,
    title: "AWS Cloud Financial Management (FinOps)",
    description: "Estratégias práticas para monitorar, controlar e reduzir custos operacionais em contas AWS sem sacrificar performance.",
    category: "Cloud",
    format: "ONLINE",
    date: "2024-09-19",
    startTime: "19:00",
    endTime: "20:30",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "AWS User Group SP",
    imageUrl: "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
    price: 0.0,
    totalSeats: 700,
    registeredParticipants: 590,
    eventUrl: "https://awsugsp.org/finops-2024",
    latitude: null,
    longitude: null
  },
  {
    id: 30,
    title: "Kotlin Functional Programming com Arrow.kt",
    description: "Conceitos de programação funcional avançada aplicados ao Kotlin: Monads, Either, Option, validação acumulatória e efeitos colaterais puros.",
    category: "Kotlin",
    format: "ONLINE",
    date: "2024-09-26",
    startTime: "20:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Kotlin Brasil",
    imageUrl: "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800",
    price: 0.0,
    totalSeats: 300,
    registeredParticipants: 240,
    eventUrl: "https://kotlin.br/arrow-functional",
    latitude: null,
    longitude: null
  }
];

let eventsStore = JSON.parse(JSON.stringify(initialEvents));

// Utility to find next ID
const getNextId = () => {
  if (eventsStore.length === 0) return 1;
  return Math.max(...eventsStore.map(e => e.id)) + 1;
};

// GET /events?offset=0&limit=10
app.get('/events', (req, res) => {
  const offset = parseInt(req.query.offset) || 0;
  const limit = parseInt(req.query.limit) || 10;

  const paginatedEvents = eventsStore.slice(offset, offset + limit);
  return res.json(paginatedEvents);
});

// GET /events/:id
app.get('/events/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const event = eventsStore.find(e => e.id === id);

  if (!event) {
    return res.status(404).json({ error: "Evento não encontrado", code: 404 });
  }

  return res.json(event);
});

// POST /events
app.post('/events', (req, res) => {
  const body = req.body;

  // Minimum required validation
  if (!body.title || !body.description || !body.category || !body.format || !body.date || !body.startTime || !body.endTime || !body.organizer) {
    return res.status(400).json({ error: "Campos obrigatórios ausentes no formulário", code: 400 });
  }

  const newEvent = {
    id: getNextId(),
    title: body.title,
    description: body.description,
    category: body.category,
    format: body.format,
    date: body.date,
    startTime: body.startTime,
    endTime: body.endTime,
    venueName: body.venueName || null,
    address: body.address || null,
    city: body.city || null,
    state: body.state || null,
    organizer: body.organizer,
    imageUrl: body.imageUrl || null,
    price: body.price !== undefined ? parseFloat(body.price) : 0.0,
    totalSeats: parseInt(body.totalSeats) || 100,
    registeredParticipants: parseInt(body.registeredParticipants) || 0,
    eventUrl: body.eventUrl || null,
    latitude: body.latitude ? parseFloat(body.latitude) : null,
    longitude: body.longitude ? parseFloat(body.longitude) : null
  };

  eventsStore.push(newEvent);
  return res.status(201).json(newEvent);
});

// PUT /events/:id
app.put('/events/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const index = eventsStore.findIndex(e => e.id === id);

  if (index === -1) {
    return res.status(404).json({ error: "Evento não encontrado para atualização", code: 404 });
  }

  const body = req.body;
  const updatedEvent = {
    ...eventsStore[index],
    title: body.title !== undefined ? body.title : eventsStore[index].title,
    description: body.description !== undefined ? body.description : eventsStore[index].description,
    category: body.category !== undefined ? body.category : eventsStore[index].category,
    format: body.format !== undefined ? body.format : eventsStore[index].format,
    date: body.date !== undefined ? body.date : eventsStore[index].date,
    startTime: body.startTime !== undefined ? body.startTime : eventsStore[index].startTime,
    endTime: body.endTime !== undefined ? body.endTime : eventsStore[index].endTime,
    venueName: body.venueName !== undefined ? body.venueName : eventsStore[index].venueName,
    address: body.address !== undefined ? body.address : eventsStore[index].address,
    city: body.city !== undefined ? body.city : eventsStore[index].city,
    state: body.state !== undefined ? body.state : eventsStore[index].state,
    organizer: body.organizer !== undefined ? body.organizer : eventsStore[index].organizer,
    imageUrl: body.imageUrl !== undefined ? body.imageUrl : eventsStore[index].imageUrl,
    price: body.price !== undefined ? parseFloat(body.price) : eventsStore[index].price,
    totalSeats: body.totalSeats !== undefined ? parseInt(body.totalSeats) : eventsStore[index].totalSeats,
    registeredParticipants: body.registeredParticipants !== undefined ? parseInt(body.registeredParticipants) : eventsStore[index].registeredParticipants,
    eventUrl: body.eventUrl !== undefined ? body.eventUrl : eventsStore[index].eventUrl,
    latitude: body.latitude !== undefined ? (body.latitude ? parseFloat(body.latitude) : null) : eventsStore[index].latitude,
    longitude: body.longitude !== undefined ? (body.longitude ? parseFloat(body.longitude) : null) : eventsStore[index].longitude
  };

  eventsStore[index] = updatedEvent;
  return res.json(updatedEvent);
});

// DELETE /events/:id
app.delete('/events/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const index = eventsStore.findIndex(e => e.id === id);

  if (index === -1) {
    return res.status(404).json({ error: "Evento não encontrado para exclusão", code: 404 });
  }

  const deletedEvent = eventsStore.splice(index, 1)[0];
  return res.status(200).json({ message: "Evento excluído com sucesso", id: deletedEvent.id });
});

// POST /reset - Restore initial 30 mock events
app.post('/reset', (req, res) => {
  eventsStore = JSON.parse(JSON.stringify(initialEvents));
  return res.json({ message: "Banco de dados mock restaurado para o estado inicial", totalEvents: eventsStore.length });
});

app.listen(PORT, () => {
  console.log(`[Tech Events Mock API] Servidor rodando na porta ${PORT}`);
  console.log(`[Endpoints]: GET /events, GET /events/:id, POST /events, PUT /events/:id, DELETE /events/:id`);
});
