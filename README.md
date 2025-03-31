# Spring Web Server Example

A Java web server example demonstrating secure communication would typically involve these key components:

- **Authentication:**
  - This ensures that only authorized users can access the server's resources. Common methods include `username`/`password` validation, and more robustly, token-based authentication like `JWT` (JSON Web Tokens).
- **Token Refresh:**
  - To enhance security and user experience, access tokens often have a limited lifespan. Token refresh mechanisms allow clients to obtain new access tokens without requiring users to re-enter their credentials. This is often done using refresh tokens.
- **WebSockets:**
  - WebSockets enable real-time, bidirectional communication between the client and server. This is crucial for applications requiring instant updates, such as chat applications or live data feeds.
- **Security Considerations:**
  - Implementing these features requires careful attention to security best practices. This includes:
    - Securely storing and handling sensitive data like passwords and tokens.
    - Using `HTTPS` to encrypt communication.
    - Validating and sanitizing user input to prevent vulnerabilities like cross-site scripting (XSS).
  - properly securing the web socket connections.
- **Java Technologies:**
  - Java Server technologies such as Spring security are often used to implement these features.
  - Java websocket API's are used to create the websocket connections.

In summary, such a Java web server would provide a secure and real-time communication channel, handling user authentication, token management, and WebSocket connections with appropriate security measures.

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
│   │   │           ├── apiResponse/
│   │   │           │   ├── ApiResponse.java
│   │   │           │   └── HttpServletErrorResponse.java
│   │   │           ├── controllers/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── CookieController.java
│   │   │           │   ├── JwtController.java
│   │   │           │   └── MainController.java
│   │   │           ├── database/
│   │   │           │   ├── DatabaseConnection.java
│   │   │           │   ├── DatabaseQueryExecution.java
│   │   │           │   └── models/
│   │   │           │       ├── User.java
│   │   │           │       └── UserPublicInfo.java
│   │   │           ├── middleware/
│   │   │           │   ├── apiGlobals/
│   │   │           │   │   └── GlobalsExceptionHandler.java
│   │   │           │   └── filters/
│   │   │           │   |   ├── AuthFilter.java
│   │   │           │   |   ├── CookieFilter.java
│   │   │           │   |   ├── FilterConfig.java
│   │   │           │   |   ├── FirstFilter.java
│   │   │           │   |   └── SecondFilter.java
│   │   │           │   └── interceptors/
│   │   │           │       ├── AuthInterceptor.java
│   │   │           │       ├── CookieInterceptor.java
│   │   │           │       ├── InterceptorConfig.java
│   │   │           │       ├── JwtInterceptor.java
│   │   │           │       └── MainInterceptor.java
│   │   │           └── utils/
│   │   │           |   ├── BcryptHashing.java
│   │   │           |   ├── JwtUtil.java
│   │   │           |   └── GenerateCookie.java
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

## About Spring Boot

Spring Boot simplifies the process of building and running Spring applications. It takes an "opinionated" approach, providing sensible defaults and auto-configuration, which drastically reduces the amount of boilerplate code developers need to write. Essentially, it streamlines Spring development by handling much of the configuration automatically, allowing developers to focus on writing application logic rather than dealing with complex setup.

Key features of Spring Boot include embedded servers (like Tomcat), "starter" dependencies that simplify build configuration, and production-ready features such as health checks and metrics. This makes it incredibly efficient for creating stand-alone, production-grade Spring-based applications that can be easily deployed. In short, Spring Boot prioritizes rapid development and ease of use, making it a popular choice for building modern Java applications.

### For Web Applications

_...from Spring website_

Spring makes building web applications fast and hassle-free. By removing much of the boilerplate code and configuration associated with web development, you get a modern web programming model that streamlines the development of server-side HTML applications, REST APIs, and bidirectional, event-based systems.

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
