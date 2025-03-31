# Spring Boot Web Server Example

Fully function web server with authentication and websocket realtime communication.

### Project Structure

```
Project: WebServer

├── .gitignore
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── file/
│   │   │           ├── controllers/
│   │   │           │   ├── ApiResponse.java
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── CookieController.java
│   │   │           │   ├── JwtController.java
│   │   │           │   ├── MainController.java
│   │   │           │   └── HttpServletErrorResponse.java
│   │   │           ├── database/
│   │   │           │   ├── DatabaseConnection.java
│   │   │           │   ├── DatabaseQueryExecution.java
│   │   │           │   └── models/
│   │   │           │       ├── User.java
│   │   │           │       └── UserPublicInfo.java
│   │   │           ├── middleware/
│   │   │           │   ├── api/
│   │   │           │   │   └── GlobalsExceptionHandler.java
│   │   │           │   └── api/filters/
│   │   │           │       ├── AuthFilter.java
│   │   │           │       ├── CookieFilter.java
│   │   │           │       ├── FilterConfig.java
│   │   │           │       ├── FirstFilter.java
│   │   │           │       └── SecondFilter.java
│   │   │           ├── middleware/
│   │   │           │   └── interceptors/
│   │   │           │       ├── AuthInterceptor.java
│   │   │           │       ├── CookieInterceptor.java
│   │   │           │       ├── InterceptorConfig.java
│   │   │           │       ├── JwtInterceptor.java
│   │   │           │       └── MainInterceptor.java
│   │   │           └── utils/
│   │   │               ├── BcryptHashing.java
│   │   │               └── GenerateCookie.java
│   │   │           ├── Main.java
│   │   │           └── webSocket/
│   │   │               ├── MainWebSocketHandler.java
│   │   │               └── WebSocketConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── GitIgnore.java
├── .idea/
└── External Libraries
```

In Spring Boot (and Spring in general), annotations like `@Configuration`, `@Component`, `@Controller`, `@Service`, `@Repository`, and others are used to identify the purpose and role of a class within the Spring application context.

Here's a breakdown of the key annotations:

1. `@Configuration`:

   - Indicates that a class provides bean definitions.
     Classes annotated with `@Configuration` can contain `@Bean` methods, which define beans that will be managed by the Spring container.
     These classes are used for Java-based configuration.

1. `@Component`:

   - A generic stereotype annotation indicating that a class is a Spring-managed component.
     Spring's component scanning mechanism automatically detects and registers classes annotated with `@Component` as beans.
   - It's a general-purpose annotation, and more specific annotations like `@Service`, `@Repository`, and `@Controller` are specialized forms of `@Component`.

1. `@Controller`:

   - A specialized form of `@Component` that indicates a class is a Spring MVC controller.
     Controllers handle web requests and define request mappings (e.g., using `@GetMapping`, `@PostMapping`).

1. `@Service`:

   - A specialized form of `@Component` that indicates a class is a service component.
     Service components typically contain business logic.
   - It's used to improve code readability and maintainability by clearly separating service layer concerns.

1. `@Repository`:

   - A specialized form of `@Component` that indicates a class is a repository component.
   - Repository components typically handle data access operations (e.g., database interactions).
   - It's used to improve code readability and maintainability by clearly separating data access layer concerns. Also it allows for automatic exception translation.

1. `@Bean`:

   - Used within @Configuration classes to define beans.
     Methods annotated with @Bean return objects that will be managed by the Spring container.

1. `@Autowired`:

   - Used for dependency injection.
   - Spring automatically injects dependencies into fields, constructor parameters, or setter methods annotated with @Autowired.

1. `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.:

   - Used in controllers to define request mappings.
   - They specify the URL paths and HTTP methods that the controller methods handle.

**How Spring Uses These Annotations:**

- Component Scanning: Spring scans the classpath for classes annotated with `@Component` and its specialized forms.
- Bean Registration: Spring registers the annotated classes as beans in the application context.
- Dependency Injection: Spring uses `@Autowired` to inject dependencies between beans.
- Request Mapping: Spring MVC uses `@RequestMapping` and related annotations to map web requests to controller methods.
- Configuration: Spring uses `@Configuration` and `@Bean` to create and manage beans defined in Java configuration classes.

In essence, these annotations provide metadata that Spring uses to understand the role and purpose of each class, enabling it to manage the application's components and their dependencies
