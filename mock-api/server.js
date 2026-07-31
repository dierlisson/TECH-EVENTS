const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const initialEvents = [
  // Eventos Encerrados/Finalizados (Passados em relação a 30/07/2026)
  {
    id: 101,
    title: "[ENCERRADO] Android Dev Conference 2025",
    description: "Edição encerrada em 2025. Evento focado em migração para Jetpack Compose, gerenciamento de memória e boas práticas de publicação no Google Play.",
    category: "Android",
    format: "PRESENCIAL",
    date: "2025-10-15",
    startTime: "09:00",
    endTime: "18:00",
    venueName: "Centro de Convenções Rebouças",
    address: "Av. Rebouças, 600 - Pinheiros",
    city: "São Paulo",
    state: "SP",
    organizer: "Android Devs SP",
    imageUrl: "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
    price: 0.0,
    totalSeats: 200,
    registeredParticipants: 200,
    eventUrl: "https://developer.android.com",
    latitude: -23.5588,
    longitude: -46.6687
  },
  {
    id: 102,
    title: "[ENCERRADO] Hackathon IA & Machine Learning 2025",
    description: "Edição encerrada em 2025. Desafio de 48 horas criando protótipos de Inteligência Artificial para solução de problemas urbanos e sociais.",
    category: "IA",
    format: "ONLINE",
    date: "2025-11-20",
    startTime: "18:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "AI Latam Group",
    imageUrl: "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=800",
    price: 0.0,
    totalSeats: 400,
    registeredParticipants: 400,
    eventUrl: "https://ailatam.org",
    latitude: null,
    longitude: null
  },
  {
    id: 103,
    title: "[ENCERRADO] Web Performance & Frontend Day 2026",
    description: "Edição realizada no início de 2026 sobre otimização de Core Web Vitals, SSR com Next.js e acessibilidade web.",
    category: "Web",
    format: "PRESENCIAL",
    date: "2026-03-10",
    startTime: "10:00",
    endTime: "17:00",
    venueName: "Hub Carioca de Inovação",
    address: "Praça Mauá, 1 - Centro",
    city: "Rio de Janeiro",
    state: "RJ",
    organizer: "Frontend Masters RJ",
    imageUrl: "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800",
    price: 49.90,
    totalSeats: 150,
    registeredParticipants: 150,
    eventUrl: "https://frontendmasters.br",
    latitude: -22.8961,
    longitude: -43.1812
  },

  // Eventos Futuros (Futuros em relação a 30/07/2026)
  {
    id: 1,
    title: "Android Dev Summit 2026 - São Paulo",
    description: "O maior evento focado em desenvolvimento Android da América Latina! Venha aprender sobre Jetpack, Kotlin Coroutines, Architecture Components, Performance e o futuro do ecossistema mobile com especialistas do mercado.",
    category: "Android",
    format: "PRESENCIAL",
    date: "2026-08-15",
    startTime: "09:00",
    endTime: "18:00",
    venueName: "Centro de Convenções Fiesp",
    address: "Av. Paulista, 1578 - Bela Vista",
    city: "São Paulo",
    state: "SP",
    organizer: "GDG São Paulo",
    imageUrl: "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
    price: 0.0,
    totalSeats: 250,
    registeredParticipants: 185,
    eventUrl: "https://developer.android.com",
    latitude: -23.5614,
    longitude: -46.6559
  },
  {
    id: 2,
    title: "Kotlin Multiplatform & AI Conference 2026",
    description: "Conferência 100% online explorando o uso de Kotlin Multiplatform (KMP) para compartilhamento de lógica entre Android e iOS, além da integração com Inteligência Artificial e Modelos LLM.",
    category: "Kotlin",
    format: "ONLINE",
    date: "2026-09-10",
    startTime: "19:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "Kotlin Brasil",
    imageUrl: "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=800",
    price: 0.0,
    totalSeats: 500,
    registeredParticipants: 340,
    eventUrl: "https://kotlinlang.org",
    latitude: null,
    longitude: null
  },
  {
    id: 3,
    title: "Backend Clean Architecture Workshop",
    description: "Imersão prática em arquitetura limpa, microsserviços, desacoplamento de código, testes unitários de alta cobertura e boas práticas de integração contínua.",
    category: "Backend",
    format: "PRESENCIAL",
    date: "2026-10-05",
    startTime: "14:00",
    endTime: "19:00",
    venueName: "ACATE Tech Park",
    address: "Rod. SC-401, 4100 - Saco Grande",
    city: "Florianópolis",
    state: "SC",
    organizer: "DevsSC",
    imageUrl: "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800",
    price: 89.90,
    totalSeats: 80,
    registeredParticipants: 62,
    eventUrl: "https://acate.com.br",
    latitude: -27.5448,
    longitude: -48.4989
  },
  {
    id: 4,
    title: "Cloud Native & Kubernetes Summit 2026",
    description: "Encontro presencial em Belo Horizonte focado em arquiteturas multicloud, gestão de clusters Kubernetes, Istio Service Mesh e automação com Terraform.",
    category: "Cloud",
    format: "PRESENCIAL",
    date: "2026-11-12",
    startTime: "09:00",
    endTime: "17:30",
    venueName: "BH TEC - Parque Tecnológico",
    address: "Rua Prof. José Vieira de Mendonça, 3011",
    city: "Belo Horizonte",
    state: "MG",
    organizer: "Cloud BH Community",
    imageUrl: "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800",
    price: 50.00,
    totalSeats: 120,
    registeredParticipants: 75,
    eventUrl: "https://cloudbh.org",
    latitude: -19.8692,
    longitude: -43.9664
  },
  {
    id: 5,
    title: "DevOps Automation & CI/CD Day 2026",
    description: "Workshop online focado na construção de esteiras de CI/CD resilientes com GitHub Actions, ArgoCD e verificações de segurança em contêineres Docker.",
    category: "DevOps",
    format: "ONLINE",
    date: "2026-12-01",
    startTime: "10:00",
    endTime: "16:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "DevOps Brasil Community",
    imageUrl: "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
    price: 0.0,
    totalSeats: 600,
    registeredParticipants: 410,
    eventUrl: "https://devopsbrasil.org",
    latitude: null,
    longitude: null
  },
  {
    id: 6,
    title: "IA Generativa & Agentes Autônomos Summit 2027",
    description: "Desenvolvimento avançado de sistemas multi-agente, modelos LLM locais e integração com aplicações móveis.",
    category: "IA",
    format: "ONLINE",
    date: "2027-01-15",
    startTime: "19:00",
    endTime: "22:00",
    venueName: null,
    address: null,
    city: null,
    state: null,
    organizer: "AI South America",
    imageUrl: "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=800",
    price: 0.0,
    totalSeats: 1000,
    registeredParticipants: 650,
    eventUrl: "https://aisouthamerica.org",
    latitude: null,
    longitude: null
  },
  {
    id: 7,
    title: "Modern Web & Performance Conference 2027",
    description: "Conferência presencial reunindo desenvolvedores frontend e engenheiros web para discutir o futuro dos browsers e otimização de performance.",
    category: "Web",
    format: "PRESENCIAL",
    date: "2027-02-20",
    startTime: "09:00",
    endTime: "18:00",
    venueName: "Lobo Coworking Curitiba",
    address: "Rua São Pedro, 460 - Cabral",
    city: "Curitiba",
    state: "PR",
    organizer: "Frontend Brasil",
    imageUrl: "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800",
    price: 120.00,
    totalSeats: 180,
    registeredParticipants: 110,
    eventUrl: "https://frontendbrasil.dev",
    latitude: -25.4123,
    longitude: -49.2567
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

// POST /reset
app.post('/reset', (req, res) => {
  eventsStore = JSON.parse(JSON.stringify(initialEvents));
  return res.json({ message: "Banco de dados mock restaurado para o estado inicial", totalEvents: eventsStore.length });
});

app.listen(PORT, () => {
  console.log(`[Tech Events Mock API] Servidor rodando na porta ${PORT}`);
  console.log(`[Endpoints]: GET /events, GET /events/:id, POST /events, PUT /events/:id, DELETE /events/:id`);
});
