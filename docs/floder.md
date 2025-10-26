```pgsql
progress-tracking-service/
├── build.gradle / pom.xml
├── Dockerfile
├── helm/                           # (Helm 차트)
│   ├── templates/
│   └── values.yaml
├── src/
│   ├── main/
│   │   ├── java/com/company/progress/
│   │   │   ├── application/               # [Application Layer]
│   │   │   │   ├── service/
│   │   │   │   │   └── ProgressTrackingService.java
│   │   │   │   ├── dto/
│   │   │   │   │   └── ProgressEventDto.java
│   │   │   │   └── consumer/
│   │   │   │       └── KafkaProgressEventConsumer.java
│   │   │   │
│   │   │   ├── domain/                    # [Domain Layer]
│   │   │   │   ├── model/
│   │   │   │   │   ├── ProgressEvent.java
│   │   │   │   │   ├── ProgressStatus.java
│   │   │   │   │   └── ProgressStep.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── ProgressRepository.java
│   │   │   │   └── service/
│   │   │   │       └── ProgressValidator.java
│   │   │   │
│   │   │   ├── infrastructure/            # [Infrastructure Layer]
│   │   │   │   ├── kafka/
│   │   │   │   │   ├── KafkaConsumerConfig.java
│   │   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   │   └── ProgressEventProducer.java
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── JpaProgressRepository.java
│   │   │   │   │   └── entity/
│   │   │   │   │       └── ProgressEventEntity.java
│   │   │   │   ├── storage/
│   │   │   │   │   └── ElasticsearchWriter.java
│   │   │   │   └── config/
│   │   │   │       └── AppConfig.java
│   │   │   │
│   │   │   ├── presentation/              # [Presentation Layer]
│   │   │   │   ├── controller/
│   │   │   │   │   └── ProgressEventController.java   # (Optional REST API)
│   │   │   │   └── api/
│   │   │   │       └── ProgressEventRequest.java
│   │   │   │
│   │   │   └── common/
│   │   │       ├── exception/
│   │   │       │   ├── InvalidProgressEventException.java
│   │   │       │   └── ErrorCode.java
│   │   │       └── util/
│   │   │           └── TraceIdGenerator.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── logback-spring.xml
│   │       └── schema/
│   │           └── progress-event-schema.json
│   │
│   └── test/
│       └── java/com/company/progress/
│           ├── unit/
│           │   └── ProgressValidatorTest.java
│           └── integration/
│               └── KafkaConsumerIntegrationTest.java
│
└── README.md
```

