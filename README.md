# Java Spring Web Server with 
## JWT Authentication, Secure Cookies, Refresh Tokens, Multi-DB, and Websockets

This repository contains a Java Spring web server implementation that demonstrates robust authentication, session management, and real-time communication capabilities.

## Features

* **JWT Authentication:** Uses JSON Web Tokens (JWT) for secure authentication and authorization.
* **Secure Cookies:** Stores JWTs in secure, HTTP-only cookies to mitigate XSS attacks.
* **Refresh Tokens:** Implements refresh tokens for seamless session renewal, enhancing security and user experience.
* **Multi-Database Support:** Configured to handle multiple database connections, allowing for flexible data management.
* **Websocket Communication:** Integrates Websockets for real-time, bidirectional communication.
* **Spring Boot:** Built using Spring Boot for rapid application development and deployment.
* **Spring Security:** Leverages Spring Security for comprehensive security features.
* **Dependency Injection:** Uses Spring's dependency injection for loose coupling and testability.
* **RESTful API:** Provides a RESTful API for client interaction.

## Technologies Used

* **Java:** Programming language.
* **Spring Boot:** Framework for rapid application development.
* **Spring Security:** Framework for authentication and authorization.
* **Spring Web:** For building web applications.
* **Spring Data JPA:** For database interaction.
* **JSON Web Token (JWT):** For token-based authentication.
* **Websocket (Spring Websocket):** For real-time communication.
* **PostgreSQL, MySQL:** For database storage.

## Setup Instructions

1.  **Clone the Repository:**
    ```bash
    git clone [repository URL]
    cd [repository directory]
    ```

2.  **Configure Database Connections:**
    * Modify the `application.properties` file to configure your database connections.
    * Specify the database URLs, usernames, passwords, and drivers.

3.  **Build the Project:**
    * Use Maven or Gradle to build the project.
        * **Maven:** `mvn clean install`

4.  **Run the Application:**
    * Run the Spring Boot application from your IDE or using the following command:
        * **Maven:** `mvn spring-boot:run`

5.  **Access the API:**
    * The application will be accessible at `http://localhost:3077`.
    * Refer to the API documentation (if provided) for endpoints and usage.

## API Endpoints (Example)

* `POST /auth/register`: Register a new user.
* `POST /auth/login`: Authenticate a user and obtain JWTs.
* `POST /auth/logout`: Logout, terminate session.
* `GET /auth/refresh-websocket-token`: JWT for web socket connection token (requires authentication).
* `POST /secure/send-websocket-message`: Send test WS message to user (requires authentication).
* `/ws?token=[JWT_token]`: Websocket endpoint for real-time communication.
* `GET /secure/get`: Test GET (requires authentication).



## Security Considerations

* JWTs are stored in secure, HTTP-only cookies to prevent client-side JavaScript access.
* Refresh tokens are used to minimize the exposure of long-lived access tokens, tokens are refreshed automatically via `interceptors`.
* Spring Security is configured to enforce proper authentication and authorization.
* Input validation and output encoding are implemented to prevent common security vulnerabilities.
* Always use HTTPS in production.

## Database setup.

* Please refer to the `application.properties` file for database connection details and 
`org/file/database/models/User.java` for the user table structure.
* 
## Websocket usage.

* Connect to the /ws endpoint using a websocket client.
* Get the JWT token from the `/auth/refresh-websocket-token` endpoint then pass in URL as `param`.
* Send test messages `POST /secure/send-websocket-message`
```json
{
    "userId": "3",
    "message": "Hello World!"
}
```
* The server will handle real time bi-directional messages.

## Future Improvements

* Implement comprehensive unit and integration tests.
* Add API documentation using Swagger or OpenAPI.
* Improve error handling and logging.
* Implement role-based access control (RBAC).
* Add more advanced websocket functionality.
* Containerize the application using Docker.
* Add CI/CD pipelines.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bug fixes or feature requests.


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
│   │   │           │   ├── DatabaseType.java
│   │   │           │   ├── DataSourceConfig.java
│   │   │           │   ├── DatabaseConnection.java
│   │   │           │   ├── DatabaseQueryExecution.java
│   │   │           │   ├── DatabaseDynamicQueryExecution.java
│   │   │           │   └── models/
│   │   │           │       ├── User.java
│   │   │           │       └── UserPublicInfo.java
│   │   │           ├── middleware/
│   │   │           │   ├── apiGlobals/
│   │   │           │   │   └── GlobalsExceptionHandler.java
│   │   │           │   └── filters/
│   │   │           │   |   ├── FilterConfig.java
│   │   │           │   |   └── CookieFilter.java
│   │   │           │   └── interceptors/
│   │   │           │       ├── AuthInterceptor.java
│   │   │           │       ├── InterceptorConfig.java
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



## License

[License, e.g., MIT License]
