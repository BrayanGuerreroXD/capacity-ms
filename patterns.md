                                            # Technology Microservice - Patrones y Arquitectura

## Tabla de Contenidos
1. [Visión General](#visión-general)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Patrones de Arquitectura](#patrones-de-arquitectura)
4. [Domain Models](#domain-models)
5. [Repositories (Puertos/Gateways)](#repositories-puertosgateways)
6. [Use Cases](#use-cases)
7. [Function Routers y Handlers](#function-routers-y-handlers)
8. [Flujo de Autenticación](#flujo-de-autenticación)
9. [Kafka (Productor y Consumidor)](#kafka-productor-y-consumidor)
10. [DTOs](#dtos)
11. [Global Exception Handler](#global-exception-handler)
12. [Configuración](#configuración)

---

## Visión General

Este proyecto es un microservicio Java/Gradle basado en **Clean Architecture** con patrones de programación reactiva. Utiliza **Spring Boot 4.0.5** con **Spring WebFlux** para APIs REST reactivas.

### Stack Tecnológico
- **Framework**: Spring Boot 4.0.5 + Spring WebFlux
- **Base de Datos**: R2DBC (MySQL)
- **Mensajeria**: Apache Kafka
- **Programación**: Reactiva (Project Reactor - Mono/Flux)
- **Build**: Gradle
- **Migraciones**: Flyway

---

## Estructura del Proyecto

```
technology-ms/
├── applications/           # Punto de entrada de la aplicación
│   └── app-service/
├── domain/                 # Capa de dominio (lógica de negocio)
│   ├── model/             # Entidades y interfaces de puertos
│   └── usecase/           # Casos de uso
├── infrastructure/         # Adaptadores y puntos de entrada
│   ├── driven-adapters/   # Adaptadores de entrada (R2DBC, Kafka Producer)
│   └── entry-points/      # Puntos de entrada (REST API, Kafka Consumer)
└── deployment/             # Docker
```

---

## Patrones de Arquitectura

### 1. Clean Architecture
Separación estricta entre:
- **Domain**: Contiene la lógica de negocio pura (models + use cases)
- **Infrastructure**: Implementaciones de adaptadores

### 2. Hexagonal Architecture (Ports & Adapters)
```
┌─────────────────────────────────────────────────────────┐
│                    infrastructure                        │
│  ┌─────────────────────┐    ┌────────────────────────┐ │
│  │    driven-adapters   │    │     entry-points        │ │
│  │  (R2DBC, Kafka      │    │  (REST API, Kafka       │ │
│  │   Publisher)        │    │   Consumer)             │ │
│  └──────────┬──────────┘    └───────────┬────────────┘ │
└──────────────┼────────────────────────────┼──────────────┘
               │                            │
          implements                   uses
               │                            │
               ▼                            ▼
┌─────────────────────────────────────────────────────────┐
│                      domain                              │
│  ┌─────────────────────┐    ┌────────────────────────┐ │
│  │       model         │    │       usecase          │ │
│  │  (entities, ports,  │    │   (business logic)     │ │
│  │   exceptions)       │    │                       │ │
│  └─────────────────────┘    └────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 3. Repository Pattern
- **Puerto**: Interfaz en `domain/model`
- **Adaptador**: Implementación en `infrastructure/driven-adapters`

### 4. Gateway Pattern
Similar al Repository Pattern pero para servicios externos (Kafka).

### 5. DTO Mapping
MapStruct para mapeo entre capas.

---

## Domain Models

### Technology.java
```java
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Technology {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Características**:
- Usa Lombok para boilerplate reduction
- Builder pattern para construcción flexible
- toBuilder para crear copias modificadas

### Auth.java
```java
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Auth {
    private Long id;
    private String email;
    private String token;
    private Integer expiresIn;
    private LocalDateTime createdAt;
}
```

### Security Models

#### LoggedUser.java
```java
@Getter
@AllArgsConstructor
public class LoggedUser {
    private final String email;
}
```
Representa el usuario autenticado.

#### UserContext.java
Interfaz reactiva para obtener el usuario actual:
```java
public interface UserContext {
    Mono<LoggedUser> getCurrentUser();
}
```

---

## Repositories (Puertos/Gateways)

### Puerto: TechnologyRepository.java
```java
public interface TechnologyRepository {
    Mono<Technology> save(Technology technology);
    Mono<Technology> update(Technology technology);
    Mono<Technology> findById(Long id);
    Mono<Technology> findByName(String name);
    Flux<Technology> findAll(int page, int size);
    Mono<Void> delete(Long id);
    Mono<Long> count();
}
```

**Patrones**:
- Retorna `Mono<>` para operaciones individuales
- Retorna `Flux<>` para colecciones
- Métodos asíncronos y no bloqueantes

### Puerto: TechnologyEventGateway.java
```java
public interface TechnologyEventGateway {
    Mono<Void> publish(Technology technology);
    Mono<Void> publishDeleted(Technology technology);
}
```
Define el contrato para publicar eventos a Kafka.

### Adaptador: TechnologyRepositoryAdapter.java
```java
@Repository
@RequiredArgsConstructor
public class TechnologyRepositoryAdapter implements TechnologyRepository {

    private final TechnologyEntityRepository entityRepository;
    private final TechnologyEntityMapper mapper;

    @Override
    public Mono<Technology> save(Technology technology) {
        return entityRepository.save(mapper.toEntity(technology))
                .map(mapper::toModel);
    }
    // ... otros métodos delegan al repositorio de R2DBC
}
```

**Patrones**:
- Implementa la interfaz del dominio
- Delega al repositorio de Spring Data R2DBC
- Usa MapStruct mapper para conversión de objetos

### Entity: TechnologyEntity.java
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("technologies")
public class TechnologyEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
```

**Características**:
- Anotaciones de Spring Data R2DBC (`@Table`, `@Column`)
- Mapeo directo a tabla de base de datos

---

## Use Cases

### Estructura
Cada caso de uso sigue el patrón:
- **Service Interface**: Define el contrato (`XxxService.java`)
- **UseCase**: Implementación del servicio (`XxxUseCase.java`)

### CreateTechnologyUseCase.java
```java
@RequiredArgsConstructor
public class CreateTechnologyUseCase implements CreateTechnologyService {

    private final TechnologyRepository technologyRepository;
    private final TechnologyEventGateway eventGateway;

    @Override
    public Mono<Technology> create(Technology technology) {
        return technologyRepository.findByName(technology.getName())
            .flatMap(existing -> Mono.<Technology>error(
                new ConflictException(GlobalExceptionEnum.TECHNOLOGY_NAME_ALREADY_EXISTS)))
            .switchIfEmpty(Mono.defer(() -> technologyRepository.save(technology)))
            .doOnSuccess(saved -> eventGateway.publish(saved)
                .subscribe(null, error -> {}));
    }
}
```

**Flujo**:
1. Verifica si existe tecnología con el mismo nombre
2. Si existe, lanza `ConflictException`
3. Si no existe, guarda la tecnología
4. Publica evento a Kafka (fire-and-forget con subscribe)

### GetTechnologyUseCase.java
```java
@RequiredArgsConstructor
public class GetTechnologyUseCase implements GetTechnologyService {

    private final TechnologyRepository technologyRepository;

    @Override
    public Mono<Technology> getById(Long id) {
        return technologyRepository.findById(id)
            .switchIfEmpty(Mono.error(
                new NotFoundException(GlobalExceptionEnum.TECHNOLOGY_NOT_FOUND)));
    }

    @Override
    public Flux<Technology> getAll(int page, int size) {
        return technologyRepository.findAll(page, size);
    }
}
```

**Patrones**:
- Usa `switchIfEmpty` para convertir Mono vacío en error
- `switchIfEmpty(Mono.error(...))` para lanzar excepciones cuando no hay resultados

### DeleteTechnologyUseCase.java
```java
@RequiredArgsConstructor
public class DeleteTechnologyUseCase implements DeleteTechnologyService {

    private final TechnologyRepository technologyRepository;
    private final TechnologyEventGateway eventGateway;

    @Override
    public Mono<Void> delete(Long id) {
        return technologyRepository.findById(id)
            .switchIfEmpty(Mono.error(
                new NotFoundException(GlobalExceptionEnum.TECHNOLOGY_NOT_FOUND)))
            .flatMap(technology -> technologyRepository.delete(id)
                .then(eventGateway.publishDeleted(technology).then()));
    }
}
```

**Flujo**:
1. Verifica que la tecnología existe
2. Elimina de la base de datos
3. Publica evento de eliminación a Kafka

---

## Function Routers y Handlers

### TechnologyRouter.java
```java
@Configuration
public class TechnologyRouter {

    private static final String PATH = "/api/v1/technologies";

    @RouterOperations({
        @RouterOperation(path = PATH, method = RequestMethod.POST, ...),
        @RouterOperation(path = PATH + "/{id}", method = RequestMethod.PUT, ...),
        // ...
    })
    @Bean
    public RouterFunction<ServerResponse> technologyRoutes(TechnologyHandler handler) {
        return RouterFunctions.route()
            .POST(PATH, accept(MediaType.APPLICATION_JSON), handler::create)
            .PUT(PATH + "/{id}", accept(MediaType.APPLICATION_JSON), handler::update)
            .GET(PATH + "/{id}", handler::getById)
            .GET(PATH, handler::getAll)
            .DELETE(PATH + "/{id}", handler::delete)
            .build();
    }
}
```

**Patrones**:
- Functional routing con Spring WebFlux
- Anotaciones `@RouterOperation` para documentación OpenAPI
- Definición declarativa de rutas

### TechnologyHandler.java
```java
@Component
@RequiredArgsConstructor
public class TechnologyHandler {

    private final CreateTechnologyService createService;
    private final UpdateTechnologyService updateService;
    private final GetTechnologyService getService;
    private final DeleteTechnologyService deleteService;
    private final TechnologyDTOMapper mapper;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(TechnologyRequest.class)
            .flatMap(dto -> createService.create(mapper.toModel(dto)))
            .map(mapper::toResponse)
            .flatMap(response -> ServerResponse.ok()
                .bodyValue(GenericResponseData.of(response)));
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        return ServerResponse.ok()
            .body(getService.getAll(page, size)
                .map(mapper::toResponse)
                .collectList()
                .map(GenericResponseData::of), GenericResponseData.class);
    }
}
```

**Patrones**:
- Recibe `ServerRequest` y retorna `Mono<ServerResponse>`
- Convierte DTO a modelo de dominio con `mapper.toModel()`
- Envuelve respuesta en `GenericResponseData`
- Parámetros de query con defaults

---

## Flujo de Autenticación

### SecurityConfig.java
```java
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthSecurityContextRepository authSecurityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .securityContextRepository(authSecurityContextRepository)
            .authorizeExchange(auth -> auth
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyExchange().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((exchange, e) ->
                    Mono.fromRunnable(() ->
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
            )
            .build();
    }
}
```

**Configuraciones**:
- CSRF deshabilitado (API stateless)
- HTTP Basic y Form Login deshabilitados
- Usa `AuthSecurityContextRepository` personalizado
- Endpoints públicos: `/actuator/**`, Swagger
- Todos los demás requieren autenticación

### AuthSecurityContextRepository.java
```java
@Component
@RequiredArgsConstructor
public class AuthSecurityContextRepository implements ServerSecurityContextRepository {

    private final GetAuthService getAuthService;

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }
        String token = authHeader.substring(7);
        return getAuthService.getByToken(token)
            .map(loggedUser -> {
                var auth = new UsernamePasswordAuthenticationToken(loggedUser, null, List.of());
                return (SecurityContext) new SecurityContextImpl(auth);
            })
            .onErrorResume(e -> Mono.empty());
    }
}
```

**Flujo de Autenticación**:
```
1. Cliente envía请求 con Header: Authorization: Bearer <token>
2. SecurityConfig usa AuthSecurityContextRepository
3. AuthSecurityContextRepository.extract(token) del header
4. Llama a GetAuthService.getByToken(token)
5. GetAuthUseCase.valida token y expiración
6. Si válido, retorna LoggedUser
7. Se crea SecurityContext con UsernamePasswordAuthenticationToken
```

### GetAuthUseCase.java
```java
@RequiredArgsConstructor
public class GetAuthUseCase implements GetAuthService {

    private final AuthRepository authRepository;

    @Override
    public Mono<LoggedUser> getByToken(String token) {
        return authRepository.findByToken(token)
            .filter(auth -> isTokenValid(auth))
            .map(auth -> new LoggedUser(auth.getEmail()))
            .switchIfEmpty(Mono.error(
                new UnauthorizedException(GlobalExceptionEnum.UNAUTHORIZED)));
    }

    private boolean isTokenValid(Auth auth) {
        if (auth.getExpiresIn() == null) return true;
        long now = Instant.now().getEpochSecond();
        long created = auth.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        return now - created < auth.getExpiresIn();
    }
}
```

---

## Kafka (Productor y Consumidor)

### Kafka Producer Configuration
```java
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        var config = new HashMap<String, Object>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS, "${kafka.bootstrap-servers}");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(...) {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

### TechnologyEventPublisherAdapter.java
```java
@Component
public class TechnologyEventPublisherAdapter implements TechnologyEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TechnologyEventMapper mapper;
    private final String topicCatalog;
    private final String topicDeleted;

    @Override
    public Mono<Void> publish(Technology technology) {
        return Mono.fromFuture(kafkaTemplate.send(topicCatalog, mapper.toEvent(technology))).then();
    }

    @Override
    public Mono<Void> publishDeleted(Technology technology) {
        return Mono.fromFuture(kafkaTemplate.send(topicDeleted, mapper.toEvent(technology))).then();
    }
}
```

**Patrones**:
- Implementa `TechnologyEventGateway`
- Usa `Mono.fromFuture()` para convertir futuro de Kafka a Mono
- Temas configurables por properties

### Kafka Consumer Configuration
```java
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, String> consumerFactory(...) {
        var config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS, "${kafka.bootstrap-servers}");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "${kafka.consumer.group-id}");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(config);
    }
}
```

### AuthLoginConsumer.java
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthLoginConsumer {

    private final SaveAuthService saveAuthService;
    private final AuthEventMapper mapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.auth-login-admin}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            AuthLoginEvent event = objectMapper.readValue(record.value(), AuthLoginEvent.class);
            saveAuthService.save(mapper.toAuth(event))
                .subscribe(null, error -> log.error("Error saving auth login: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing auth.login.admin message: {}", e.getMessage());
        }
    }
}
```

**Flujo de Eventos**:
```
┌──────────────┐    Kafka Topic     ┌─────────────────┐
│ Auth Service │ ─────────────────► │ technology-ms   │
│   (Externo)  │  auth-login-admin  │ AuthLoginConsumer│
└──────────────┘                     └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │  SaveAuthUseCase │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │  AuthRepository  │
                                    │    (R2DBC)       │
                                    └─────────────────┘
```

---

## DTOs

### TechnologyRequest.java (Input DTO)
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnologyRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 90, message = "Description must be less than 90 characters")
    private String description;
}
```

**Patrones**:
- Jakarta Validation annotations (`@NotBlank`, `@Size`)
- Mensajes de error personalizados
- Lombok para boilerplate

### GenericResponseData.java (Wrapper Response)
```java
@Getter
@AllArgsConstructor
public class GenericResponseData<T> {
    private final T data;

    public static <T> GenericResponseData<T> of(T data) {
        return new GenericResponseData<>(data);
    }
}
```

**Estructura de Respuesta**:
```json
{
  "data": { ... }
}
```

### TechnologyDTOMapper.java (MapStruct)
```java
@Mapper(componentModel = "spring")
public interface TechnologyDTOMapper {
    Technology toModel(TechnologyRequest request);
    TechnologyResponse toResponse(Technology technology);
}
```

---

## Global Exception Handler

### GlobalExceptionEnum.java
```java
@Getter
@RequiredArgsConstructor
public enum GlobalExceptionEnum {
    TECHNOLOGY_NOT_FOUND("Technology not found", "No technology found with the provided identifier"),
    TECHNOLOGY_NAME_ALREADY_EXISTS("Technology name already exists", "..."),
    INVALID_TECHNOLOGY_ID("Invalid technology ID", "..."),
    UNAUTHORIZED("Unauthorized", "Token is missing or invalid"),
    TOKEN_EXPIRED("Token expired", "The provided token has expired");

    private final String message;
    private final String description;
}
```

### Custom Exceptions
```java
public class NotFoundException extends RuntimeException {
    private final GlobalExceptionEnum error;
    // Constructor y getters
}

public class ConflictException extends RuntimeException {
    private final GlobalExceptionEnum error;
}

public class UnauthorizedException extends RuntimeException {
    private final GlobalExceptionEnum error;
}

public class BadRequestException extends RuntimeException {
    private final GlobalExceptionEnum error;
}
```

### GlobalExceptionHandler.java
```java
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        GenericResponseData<ErrorData> body = buildErrorBody(ex);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(body))
            .flatMap(bytes -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))));
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof BadRequestException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof ConflictException) return HttpStatus.CONFLICT;
        if (ex instanceof UnauthorizedException) return HttpStatus.UNAUTHORIZED;
        if (ex instanceof MethodArgumentNotValidException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
```

### ErrorData.java
```java
@Getter
@AllArgsConstructor
public class ErrorData {
    private final String errorCode;
    private final String message;
    private final String description;

    public static ErrorData of(GlobalExceptionEnum error) {
        return new ErrorData(error.name(), error.getMessage(), error.getDescription());
    }
}
```

**Flujo de Errores**:
```
UseCase lanza Exception
         │
         ▼
GlobalExceptionHandler.handle()
         │
         ▼
resolveStatus() → HttpStatus
buildErrorBody() → GenericResponseData<ErrorData>
         │
         ▼
Respuesta JSON:
{
  "data": {
    "errorCode": "TECHNOLOGY_NOT_FOUND",
    "message": "Technology not found",
    "description": "No technology found with..."
  }
}
```

---

## Configuración

### application.yml
```yaml
server:
  port: 7600

spring:
  r2dbc:
    url: r2dbc:mysql://localhost:3306/db_technology
    username: root
    password: password
  flyway:
    locations: classpath:db/migration

management:
  endpoints:
    web:
      exposure:
        include: health,prometheus

kafka:
  bootstrap-servers: localhost:9092
  topics:
    sync-technology-catalog: sync-technology-catalog
    sync-technology-deleted: sync-technology-deleted
    auth-login-admin: auth-login-admin
    generic-auth-logout: generic-auth-logout
  consumer:
    group-id: technology-ms-group
```

### UseCasesConfig.java
```java
@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
    @Bean
    public ReactorCoreProcessor<Technology> technologyProcessor() {
        return ReactorCoreProcessor.create();
    }
}
```

---

## Resumen de Patrones

| Patrón | Implementación |
|--------|---------------|
| Clean Architecture | Separación domain/infrastructure |
| Hexagonal | Puertos en domain, adaptadores en infrastructure |
| Repository | `XxxRepository` (puerto) + `XxxRepositoryAdapter` (implementación) |
| Gateway | `XxxGateway` (puerto) + `XxxPublisherAdapter` (implementación) |
| Use Case | Service interface + UseCase class |
| DTO Mapping | MapStruct con `@Mapper(componentModel = "spring")` |
| Functional Routing | Spring WebFlux RouterFunctions |
| Exception Enum | `GlobalExceptionEnum` centraliza todos los códigos de error |
| Reactive | Project Reactor (Mono/Flux) en toda la cadena |
| Security | Spring Security WebFlux con `ServerSecurityContextRepository` |
| Kafka Producer | `KafkaTemplate<String, Object>` con `Mono.fromFuture()` |
| Kafka Consumer | `@KafkaListener` con deserialización manual |
