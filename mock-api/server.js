const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const initialEvents = [
  // Eventos Encerrados / Finalizados (Passados)
  {
    id: 101,
    title: "[ENCERRADO] Android Dev Conference 2023",
    description: "Edição encerrada de 2023. Evento focado em migração para Jetpack Compose, gerenciamento de memória e boas práticas de publicação no Google Play.",
    category: "Android",
    format: "PRESENCIAL",
    date: "2023-10-15",
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
    title: "[ENCERRADO] Hackathon IA & Machine Learning 2023",
    description: "Edição encerrada. Desafio de 48 horas criando protótipos de Inteligência Artificial para solução de problemas urbanos e sociais.",
    category: "IA",
    format: "ONLINE",
    date: "2023-11-20",
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
    title: "[ENCERRADO] Web Performance & Frontend Day 2024",
    description: "Evento realizado no início de 2024 sobre otimização de Core Web Vitals, SSR com Next.js e acessibilidade web.",
    category: "Web",
    format: "PRESENCIAL",
    date: "2024-02-10",
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

  // Eventos Futuros
  {
    id: 1,
    title: "Android Dev Summit 2024 - São Paulo",
    description: "Junte-se aos principais especialistas em Android do Brasil para palestras profundas sobre Jetpack, arquitetura MVVM, Kotlin Coroutines, otimização de performance e novidades da plataforma.",
    category: "Android",
    format: "PRESENCIAL",
    date: "2024-11-15",
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
    title: "Kotlin Multiplatform & AI Conference",
    description: "Aprenda como compartilhar código de negócio entre Android, iOS e Backend utilizando Kotlin Multiplatform (KMP) com exemplos práticos e arquitetura limpa.",
    category: "Kotlin",
    format: "ONLINE",
    date: "2024-11-20",
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
    date: "2024-12-05",
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
    title: "Cloud Native & Kubernetes Summit 2025",
    description: "Encontro presencial em Belo Horizonte focado em arquiteturas multicloud, gestão de clusters Kubernetes, Istio Service Mesh e automação com Terraform.",
    category: "Cloud",
    format: "PRESENCIAL",
    date: "2025-01-18",
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
    title: "DevOps Automation & CI/CD Day 2025",
    description: "Workshop online focado na construção de esteiras de CI/CD resilientes com GitHub Actions, ArgoCD e verificações de segurança em contêineres Docker.",
    category: "DevOps",
    format: "ONLINE",
    date: "2025-02-22",
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
