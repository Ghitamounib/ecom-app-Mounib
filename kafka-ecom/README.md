# 🚀 Apache Kafka avec Spring Cloud Stream

Ce projet démontre l'utilisation d'Apache Kafka avec Spring Boot pour le traitement de flux de messages en temps réel, incluant production, consommation, et traitement avec Kafka Streams.

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Partie 1 : Producer & Consumer CLI](#partie-1--producer--consumer-cli)
- [Partie 2 : API REST Producer](#partie-2--api-rest-producer)
- [Partie 3 : Kafka Streams & Analytics](#partie-3--kafka-streams--analytics)
- [Configuration](#configuration)
- [Tests](#tests)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Vue d'ensemble

Ce projet illustre trois aspects fondamentaux de Kafka :

| Partie | Description | Technologies |
|--------|-------------|--------------|
| **Partie 1** | Production et consommation de messages via CLI | Kafka CLI Tools |
| **Partie 2** | Exposition d'une API REST pour publier des messages | Spring Cloud Stream, StreamBridge |
| **Partie 3** | Analyse en temps réel avec visualisation | Kafka Streams, SSE, Graphiques dynamiques |

### Fonctionnalités principales

✅ **Production de messages** via CLI et API REST  
✅ **Consommation de messages** en temps réel  
✅ **Traitement de flux** avec Kafka Streams  
✅ **Analytics en temps réel** avec fenêtres glissantes  
✅ **Visualisation dynamique** via Server-Sent Events (SSE)  
✅ **State Store** pour le comptage des visites  

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       Frontend (Browser)                    │
│                  Graphiques dynamiques (SSE)                │
└────────────────────────┬────────────────────────────────────┘
                         │ SSE Stream
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                  Spring Boot Application                    │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ REST         │  │ Kafka        │  │ Kafka        │       │
│  │ Controller   │  │ Producer     │  │ Consumer     │       │
│  │              │  │ (StreamBridge│  │              │       │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘       │
│         │                  │                │               │
│         └──────────────────┼────────────────┘               │
│                            │                                │
│                   ┌────────▼────────┐                       │
│                   │ Kafka Streams   │                       │
│                   │ (count-store)   │                       │
│                   └─────────────────┘                       │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│              Apache Kafka Broker (Docker)                   │
│                                                             │
│  Topics: R2, R4, P1, P2                                     │
│  Port: 9092                                                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Prérequis

- **Docker** & **Docker Compose**
- **Java** 17 ou supérieur
- **Maven** 3.8+
- **Spring Boot** 3.x
- **Apache Kafka** (via Docker)

### Dépendances Maven

```xml
<dependencies>
    <!-- Spring Cloud Stream Kafka -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream-binder-kafka-streams</artifactId>
    </dependency>
    
    <!-- Spring Cloud Stream -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream</artifactId>
    </dependency>
    
    <!-- Kafka Streams -->
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-streams</artifactId>
    </dependency>
    
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

---

## 📡 Partie 1 : Producer & Consumer CLI

Cette section couvre les bases de Kafka en utilisant les outils en ligne de commande pour produire et consommer des messages.

### Étape 1 : Vérifier que Kafka est en cours d'exécution

```bash
docker ps
```

**Résultat attendu :**
```
CONTAINER ID   IMAGE                    STATUS       PORTS                    NAMES
abc123def456   confluentinc/cp-kafka    Up 2 hours   0.0.0.0:9092->9092/tcp  bdcc-kafka-broker
```

> ⚠️ Le conteneur **bdcc-kafka-broker** doit apparaître avec le statut `Up`.

### Étape 2 : Créer un topic (optionnel)

```bash
docker exec -it bdcc-kafka-broker kafka-topics \
  --create \
  --bootstrap-server broker:9092 \
  --topic R2 \
  --partitions 3 \
  --replication-factor 1
```

### Étape 3 : Lister les topics existants

```bash
docker exec -it bdcc-kafka-broker kafka-topics \
  --list \
  --bootstrap-server broker:9092
```

### Étape 4 : Produire des messages (Producer)

Ouvrez un terminal et exécutez :

```bash
docker exec -it bdcc-kafka-broker kafka-console-producer \
  --broker-list broker:9092 \
  --topic R2
```

Ensuite, tapez des messages (un par ligne) :

```
> Hello Kafka
> Message 1
> Message 2
> Test de production
```

> 💡 **Astuce** : Appuyez sur `Ctrl+C` pour quitter le producer.

### Étape 5 : Consommer des messages (Consumer)

Dans un **second terminal**, lancez le consumer :

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server broker:9092 \
  --topic R2 \
  --from-beginning
```

**Résultat attendu :**
```
Hello Kafka
Message 1
Message 2
Test de production
```

### Options avancées du Consumer

#### Afficher les clés et valeurs

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server broker:9092 \
  --topic R4 \
  --from-beginning \
  --property print.key=true \
  --property print.value=true \
  --property key.separator=" : "
```

#### Consommer avec des désérialiseurs spécifiques

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server broker:9092 \
  --topic R4 \
  --property print.key=true \
  --property print.value=true \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

---

## 🌐 Partie 2 : API REST Producer

Cette partie expose une API REST permettant de publier des messages dans Kafka via Spring Cloud Stream.

### Architecture

```
Client HTTP
    ↓
REST Controller (/publish/{topic}/{name})
    ↓
StreamBridge.send()
    ↓
Kafka Topic (R4, R2, etc.)
    ↓
Kafka Consumer (CLI ou Application)
```

### Implémentation

#### REST Controller

```java
@RestController
@RequestMapping("/api")
public class KafkaProducerController {
    
    private final StreamBridge streamBridge;
    
    public KafkaProducerController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }
    
    @PostMapping("/publish/{topic}/{name}")
    public ResponseEntity<Map<String, String>> publish(
            @PathVariable String topic,
            @PathVariable String name) {
        
        // Création de l'événement
        PageEvent event = new PageEvent(name, new Date(), System.currentTimeMillis());
        
        // Envoi vers Kafka
        streamBridge.send(topic, event);
        
        return ResponseEntity.ok(Map.of(
            "status", "Message publié avec succès",
            "topic", topic,
            "name", name
        ));
    }
}
```

#### Modèle de données

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageEvent {
    private String name;       // Nom de la page (P1, P2, etc.)
    private Date date;         // Date de l'événement
    private Long timestamp;    // Timestamp Unix
}
```

### Configuration application.yml

```yaml
spring:
  cloud:
    stream:
      kafka:
        binder:
          brokers: localhost:9092
    function:
      definition: pageEventConsumer
```

### Tests de l'API

#### 1. Publier un message via cURL

```bash
# Publication sur le topic R4 avec le nom "P1"
curl -X POST http://localhost:8080/api/publish/R4/P1

# Publication sur le topic R4 avec le nom "P2"
curl -X POST http://localhost:8080/api/publish/R4/P2

# Publication multiple
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/publish/R4/P1
  sleep 1
done
```

#### 2. Vérifier la réception dans Kafka

Dans un terminal, lancez le consumer :

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server broker:9092 \
  --topic R4 \
  --from-beginning \
  --property print.key=true \
  --property print.value=true
```

**Résultat attendu :**
```
P1 : {"name":"P1","date":"2024-12-13T10:30:45.123Z","timestamp":1702467045123}
P2 : {"name":"P2","date":"2024-12-13T10:30:46.456Z","timestamp":1702467046456}
P1 : {"name":"P1","date":"2024-12-13T10:30:47.789Z","timestamp":1702467047789}
```

#### 3. Test avec Postman

**Méthode** : `POST`  
**URL** : `http://localhost:8080/api/publish/R4/P1`  
**Headers** : `Content-Type: application/json`

**Réponse** :
```json
{
  "status": "Message publié avec succès",
  "topic": "R4",
  "name": "P1"
}
```

---

## 📊 Partie 3 : Kafka Streams & Analytics

Cette partie implémente un système d'analytics en temps réel pour compter les visites de pages avec Kafka Streams.

### Objectif

Compter en temps réel le nombre de visites des pages **P1** et **P2** en utilisant :
- **Kafka Streams** pour le traitement
- **State Store** (`count-store`) pour le stockage des compteurs
- **Fenêtres glissantes** de 5 secondes
- **Server-Sent Events (SSE)** pour le streaming vers le frontend

### Architecture du traitement

```
Topic: R4 (PageEvent)
    ↓
Kafka Streams Processor
    ↓
Fenêtre glissante (5 secondes)
    ↓
Agrégation (count)
    ↓
State Store: count-store
    ↓
SSE Endpoint → Frontend
    ↓
Graphiques en temps réel
```

### Implémentation

#### Configuration Kafka Streams

```java
@Configuration
public class KafkaStreamsConfig {
    
    @Bean
    public Function<KStream<String, PageEvent>, KStream<String, Long>> kStreamFunction() {
        return input -> input
            // Grouper par nom de page
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(PageEvent.class)))
            
            // Fenêtre glissante de 5 secondes
            .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
            
            // Compter les événements
            .count(Materialized.as("count-store"))
            
            // Transformer en KStream
            .toStream()
            
            // Extraire la clé de la fenêtre
            .map((windowedKey, count) -> 
                new KeyValue<>(windowedKey.key(), count));
    }
}
```

#### Service d'Analytics

```java
@Service
public class AnalyticsService {
    
    @Autowired
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    
    public Map<String, Long> getPageViewCounts() {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        
        if (kafkaStreams == null) {
            return Map.of();
        }
        
        ReadOnlyKeyValueStore<String, Long> store = kafkaStreams
            .store(StoreQueryParameters.fromNameAndType(
                "count-store", 
                QueryableStoreTypes.keyValueStore()
            ));
        
        Map<String, Long> counts = new HashMap<>();
        
        try (KeyValueIterator<String, Long> iterator = store.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, Long> entry = iterator.next();
                counts.put(entry.key, entry.value);
            }
        }
        
        return counts;
    }
}
```

#### REST Controller avec SSE

```java
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    // Endpoint REST classique
    @GetMapping("/page-views")
    public ResponseEntity<Map<String, Long>> getPageViews() {
        return ResponseEntity.ok(analyticsService.getPageViewCounts());
    }
    
    // Endpoint SSE pour streaming en temps réel
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> streamPageViews() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(tick -> analyticsService.getPageViewCounts());
    }
}
```

### Configuration complète (application.yml)

```yaml
spring:
  application:
    name: kafka-streams-app
    
  cloud:
    stream:
      kafka:
        binder:
          brokers: localhost:9092
          
        streams:
          binder:
            configuration:
              commit.interval.ms: 1000
              default.key.serde: org.apache.kafka.common.serialization.Serdes$StringSerde
              default.value.serde: org.springframework.kafka.support.serializer.JsonSerde
              
      bindings:
        kStreamFunction-in-0:
          destination: R4
          group: analytics-group
          
        kStreamFunction-out-0:
          destination: R4-analytics
          
    function:
      definition: kStreamFunction

server:
  port: 8080
```

### Frontend - Visualisation en temps réel

#### HTML + JavaScript (Chart.js)

```html
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Analytics Kafka - Visites en temps réel</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 20px;
            background: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .chart-container {
            position: relative;
            height: 400px;
            margin-top: 30px;
        }
        .stats {
            display: flex;
            justify-content: space-around;
            margin-top: 20px;
        }
        .stat-card {
            background: #4CAF50;
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            flex: 1;
            margin: 0 10px;
        }
        .stat-card h3 {
            margin: 0;
            font-size: 2em;
        }
        .stat-card p {
            margin: 5px 0 0 0;
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📊 Analytics Kafka - Visites en temps réel</h1>
        
        <div class="stats">
            <div class="stat-card">
                <h3 id="p1-count">0</h3>
                <p>Visites P1</p>
            </div>
            <div class="stat-card" style="background: #2196F3;">
                <h3 id="p2-count">0</h3>
                <p>Visites P2</p>
            </div>
            <div class="stat-card" style="background: #FF9800;">
                <h3 id="total-count">0</h3>
                <p>Total</p>
            </div>
        </div>
        
        <div class="chart-container">
            <canvas id="pageViewsChart"></canvas>
        </div>
    </div>

    <script>
        // Configuration du graphique
        const ctx = document.getElementById('pageViewsChart').getContext('2d');
        const chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['P1', 'P2'],
                datasets: [{
                    label: 'Nombre de visites',
                    data: [0, 0],
                    backgroundColor: [
                        'rgba(76, 175, 80, 0.8)',
                        'rgba(33, 150, 243, 0.8)'
                    ],
                    borderColor: [
                        'rgba(76, 175, 80, 1)',
                        'rgba(33, 150, 243, 1)'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                animation: {
                    duration: 500
                }
            }
        });

        // Connexion SSE
        const eventSource = new EventSource('http://localhost:8080/api/analytics/stream');
        
        eventSource.onmessage = function(event) {
            const data = JSON.parse(event.data);
            
            const p1Count = data.P1 || 0;
            const p2Count = data.P2 || 0;
            const total = p1Count + p2Count;
            
            // Mise à jour des cartes
            document.getElementById('p1-count').textContent = p1Count;
            document.getElementById('p2-count').textContent = p2Count;
            document.getElementById('total-count').textContent = total;
            
            // Mise à jour du graphique
            chart.data.datasets[0].data = [p1Count, p2Count];
            chart.update();
        };
        
        eventSource.onerror = function(error) {
            console.error('Erreur SSE:', error);
        };
    </script>
</body>
</html>
```

### Tests complets

#### 1. Simuler des visites

Script bash pour générer du trafic :

```bash
#!/bin/bash

echo "🚀 Génération de trafic Kafka..."

# Boucle infinie
while true; do
    # Génération aléatoire de visites P1 ou P2
    PAGE=$((RANDOM % 2 + 1))
    
    curl -X POST "http://localhost:8080/api/publish/R4/P$PAGE" -s > /dev/null
    echo "✅ Visite P$PAGE envoyée"
    
    # Pause aléatoire entre 0.5 et 2 secondes
    sleep $((RANDOM % 15 + 5))0.1
done
```

Rendez le script exécutable et lancez-le :

```bash
chmod +x generate_traffic.sh
./generate_traffic.sh
```

#### 2. Vérifier le State Store

```bash
# Via l'API REST
curl http://localhost:8080/api/analytics/page-views

# Résultat attendu
{
  "P1": 15,
  "P2": 23
}
```

#### 3. Observer le flux SSE

```bash
curl -N http://localhost:8080/api/analytics/stream
```

**Sortie attendue (stream continu) :**
```
data:{"P1":15,"P2":23}

data:{"P1":16,"P2":23}

data:{"P1":16,"P2":24}
```

---

## ⚙️ Configuration

### Topics Kafka utilisés

| Topic | Description | Clé | Valeur |
|-------|-------------|-----|--------|
| **R2** | Messages simples (CLI) | - | String |
| **R4** | Événements de pages | String (P1, P2) | PageEvent (JSON) |
| **R4-analytics** | Résultats agrégés | String | Long (count) |

### Ports utilisés

| Service | Port | Description |
|---------|------|-------------|
| **Kafka Broker** | 9092 | Broker Kafka |
| **Zookeeper** | 2181 | Coordination Kafka |
| **Spring Boot App** | 8080 | API REST + SSE |

---

## 🧪 Tests

### Test du Producer CLI

```bash
# Terminal 1 : Producer
docker exec -it bdcc-kafka-broker kafka-console-producer \
  --broker-list broker:9092 \
  --topic R2

# Terminal 2 : Consumer
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server broker:9092 \
  --topic R2 \
  --from-beginning
```

### Test de l'API REST

```bash
# Test simple
curl -X POST http://localhost:8080/api/publish/R4/P1

# Test avec boucle
for i in {1..20}; do
  curl -X POST http://localhost:8080/api/publish/R4/P1
  curl -X POST http://localhost:8080/api/publish/R4/P2
  sleep 0.5
done
```

### Test du State Store

```bash
# Récupérer les compteurs
curl http://localhost:8080/api/analytics/page-views | jq

# Stream SSE
curl -N http://localhost:8080/api/analytics/stream
```

---

## 🔧 Troubleshooting

### Problème : Kafka ne démarre pas

```bash
# Vérifier les logs
docker logs bdcc-kafka-broker

# Redémarrer le conteneur
docker restart bdcc-kafka-broker
```

### Problème : Le State Store est vide

```bash
# Vérifier l'état de Kafka Streams
curl http://localhost:8080/actuator/health

# Recréer le State Store
docker exec -it bdcc-kafka-broker kafka-streams-application-reset \
  --application-id kafka-streams-app \
  --bootstrap-servers broker:9092
```

### Problème : Messages non reçus

```bash
# Vérifier les topics
docker exec -it bdcc-kafka-broker kafka-topics \
  --list \
  --bootstrap-server broker:9092

# Vérifier les consumer groups
docker exec -it bdcc-kafka-broker kafka-consumer-groups \
  --list \
  --bootstrap-server broker:9092
```

### Problème : SSE ne se connecte pas

1. Vérifier CORS dans le controller :

```java
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    // ...
}
```

2. Tester avec curl :

```bash
curl -N -H "Accept: text/event-stream" \
  http://localhost:8080/api/analytics/stream
```

---

## 📚 Concepts clés

### Fenêtres glissantes (Tumbling Windows)

```java
TimeWindows.of(Duration.ofSeconds(5))
```

- Crée des fenêtres de 5 secondes
- Comptage réinitialisé à chaque fenêtre
- Permet l'analyse temporelle

### State Store

- Stockage clé-valeur local
- Maintient l'état des agrégations
- Interrogeable via `ReadOnlyKeyValueStore`
- Sauvegardé automatiquement

### StreamBridge

- Interface pour publier des messages
- Découplage du code et de Kafka
- Gestion automatique de la sérialisation

---

## 🚀 Améliorations futures

- [ ] Ajouter Kafka Connect pour l'intégration de bases de données
- [ ] Implémenter des fenêtres coulissantes (Sliding Windows)
- [ ] Ajouter l'authentification SASL/SSL
- [ ] Créer un dashboard React pour les analytics
- [ ] Implémenter Schema Registry pour la gestion des schémas
- [ ] Ajouter des alertes en temps réel
- [ ] Implémenter KSQL pour des requêtes SQL sur les streams
- [ ] Ajouter des tests d'intégration avec Testcontainers

---

## 📖 Ressources

- [Documentation Kafka](https://kafka.apache.org/documentation/)
- [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)
- [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [Chart.js](https://www.chartjs.org/)

---

## 📝 Licence

Ce projet est à usage éducatif.

---

## 👥 Contributeurs

Développé dans le cadre d'un projet d'apprentissage d'Apache Kafka et du traitement de flux en temps réel.
