# Modern Java Spring Web Server Architecture 🚀
## Enterprise-grade Authentication, Real-time Communication & Multi-Database Solution

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat&logo=java)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Latest-success?style=flat&logo=springsecurity)](https://spring.io/projects/spring-security)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-blue?style=flat&logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat)](LICENSE)
[![GitHub](https://img.shields.io/badge/GitHub-MrDay2Day-black?style=flat&logo=github)](https://github.com/MrDay2Day)

Welcome to the definitive implementation of a production-ready Java Spring Web Server that solves the complex challenges of modern web application development. This project demonstrates industry best practices for security, stateless authentication, and bidirectional communication.

## Core Capabilities 💪

* **Stateless Authentication with JWT** 🔐 - Implements the OAuth 2.0 authorization framework using JSON Web Tokens for secure, scalable authentication
* **Defense-in-Depth Security** 🛡️ - Protects authentication tokens with HTTP-only secure cookies, mitigating XSS vulnerabilities 
* **Seamless Session Management** ⚡ - Transparent token refresh mechanism maintains user sessions without disrupting experience
* **Polyglot Persistence** 💾 - Configurable connections to multiple database systems (PostgreSQL, MySQL) with transaction support
* **Real-time Bidirectional Communication** 📡 - Full WebSocket implementation for instant data exchange and notifications
* **Production-Ready Architecture** 🏗️ - Built on Spring Boot's enterprise-grade foundation with comprehensive security controls

## Technical Stack 📚

* **Java 17+** ☕ - Modern language features including records, pattern matching, and enhanced switch expressions
* **Spring Boot 3.x** 🍃 - Streamlined application bootstrapping and configuration
* **Spring Security** 🔒 - Comprehensive security framework with secure defaults
* **Spring WebFlux** ⚛️ - Reactive programming model for highly concurrent applications
* **Spring Data JPA** 📊 - Advanced ORM with sophisticated query capabilities
* **JWT Library** 🎟️ - Industry-standard JWT implementation with robust signature verification
* **Spring WebSocket** 🔌 - Enterprise-grade WebSocket implementation with STOMP messaging
* **Hibernate** 🗄️ - Feature-rich JPA provider with extensive customization options

## Getting Started 🏁

### Prerequisites
* JDK 17+ 
* Maven 3.8+
* PostgreSQL/MySQL instance

### Quick Setup

1. **Clone & Navigate:** 📂
   ```bash
   git clone https://github.com/MrDay2Day/spring-advanced-webserver.git
   cd spring-advanced-webserver
   ```

2. **Configure Your Environment:** ⚙️
   Create an `application-dev.properties` file based on the template below:

   ```properties
   # Environment Variables
   
   # Spring Server Variables
   
   server.port=3077
   server.tomcat.max-http-header-size=1048576
   
   # JWT Variables
   
   jwt.secret=your-very-long-and-secure-secret-key
   jwt.refresh.secret=your-very-long-and-secure-secret-key-for-refresh
   jwt.websocket.secret=your-very-long-and-secure-secret-key-for-websocket
   
   jwt.cookie.name=jwtToken
   jwt.expiration.seconds=30
   
   jwt.cookie.refresh.name=jwtRefreshToken
   jwt.expiration.refresh.seconds=5184000
   
   jwt.cookie.secret=this_is_a_secure_string_to_sign_cookies_from_this_server
   
   # PostGreSQL Variables
   
   postgresql.conn.host=postgresql_host
   postgresql.conn.database=database
   postgresql.conn.username=username
   postgresql.conn.password=password
   
   # MySQL Variables
   
   mysql.conn.host=mysql_host
   mysql.conn.database=database
   mysql.conn.username=username
   mysql.conn.password=password
   
   # HikariCP (Connection Pool) Settings (Optional but Recommended)
   spring.datasource.hikari.maximum-pool-size=10
   spring.datasource.hikari.minimum-idle=2
   spring.datasource.hikari.idle-timeout=30000
   spring.datasource.hikari.connection-timeout=30000
   ```

3. **Build & Run:** 🛠️
   ```bash
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Verify Installation:** ✅
   The server will start at `http://localhost:3077`

## API Reference 📘

### Authentication Endpoints 🔑

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|-------------|----------|
| `/auth/register` | POST | Create new user account | `{"username":"user","password":"pass","email":"user@example.com"}` | User details with 201 status |
| `/auth/login` | POST | Authenticate user | `{"username":"user","password":"pass"}` | Sets HTTP-only cookies, returns user profile |
| `/auth/logout` | POST | End user session | None | Clears auth cookies, returns 200 status |
| `/auth/refresh-websocket-token` | GET | Generate WebSocket token | None (requires auth cookie) | `{"token":"ws-jwt-token"}` |

### Secure API Endpoints 🔒

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/secure/get` | GET | Test authenticated access | Required |
| `/secure/send-websocket-message` | POST | Send real-time message | Required |

### WebSocket Communication 📡

Connect to the WebSocket endpoint with your authentication token:
```
ws://localhost:3077/ws?token={your-ws-token}
```

Send a test message through the REST API:

`POST /secure/send-websocket-message`
```json
{
  "userId": "3",
  "message": "Real-time notification test"
}
```

## Architecture Deep-Dive 🔍

### Authentication Flow 🔄

1. **Registration**: User credentials are securely hashed with BCrypt before storage
2. **Login**: Credentials verified, JWT tokens generated (access + refresh)
3. **Token Storage**: JWTs stored in HTTP-only cookies with secure and SameSite flags
4. **Auto-Refresh**: Interceptors transparently refresh tokens before expiration
5. **WebSocket Auth**: Specialized short-lived tokens for WebSocket connections

### Security Implementation 🛡️

* **CSRF Protection**: Spring Security's CSRF token validation
* **XSS Mitigation**: Content-Security-Policy headers and HTTP-only cookies
* **Input Validation**: Bean Validation (JSR 380) for request payload validation
* **Rate Limiting**: Custom interceptors prevent brute force attacks
* **Secure Headers**: Implements OWASP recommended security headers

### Database Architecture 💾

The multi-database configuration enables:
* Separation of concerns (e.g., user data vs. application data)
* Cross-database transactions with JTA when needed
* Database-specific optimization strategies
* Read-write splitting for high-load scenarios

### WebSocket Implementation 🔌

Our WebSocket implementation provides:
* Authenticated connections with JWT verification
* STOMP messaging protocol for pub/sub capabilities
* Message filtering based on user context
* Reconnection handling with session recovery
* Optimized broadcast capabilities for high-volume messaging

## Spring Boot Essentials 🍃

Spring Boot revolutionizes Java web development through:

### Convention Over Configuration ⚙️
Spring Boot eliminates boilerplate by providing sensible defaults while allowing customization where needed. This approach dramatically reduces development time and cognitive overhead.

### Embedded Application Server 📦
The embedded Tomcat/Jetty/Undertow server eliminates deployment complexity and enables true "java -jar" deployment with minimal configuration.

### Auto-Configuration 🔄
Spring Boot analyzes your classpath and automatically configures components based on detected libraries, reducing configuration to the absolute minimum.

### Production-Ready Features 🚀
Built-in actuator endpoints provide metrics, health checks, and environment information essential for production monitoring.

### Dependency Management 📚
Spring Boot carefully curates compatible dependency versions, eliminating "dependency hell" and ensuring components work together seamlessly.

### Spring Annotation Deep-Dive 🔍

Spring's annotation-based programming model provides clear component classification:

* **@Configuration**: Classes that define beans through @Bean methods
* **@Component**: Generic Spring-managed component
* **@Controller/@RestController**: Web request handlers
* **@Service**: Business logic encapsulation
* **@Repository**: Data access components with exception translation
* **@Entity**: JPA-managed database entity
* **@Autowired**: Dependency injection marker (constructor injection preferred)
* **@RequestMapping/@GetMapping/@PostMapping**: HTTP request mapping
* **@ExceptionHandler**: Centralized exception management

## Advanced Features ✨

### Asynchronous Processing ⏱️
The application demonstrates Spring's @Async capabilities for background processing tasks.

### Caching 💨
Strategic caching with Spring Cache and EhCache reduces database load for frequently accessed data.

### Comprehensive Testing 🧪
Includes unit, integration, and end-to-end tests with JUnit 5, Mockito, and Spring Test.

### Advanced WebSocket Features 📡
* Binary message support
* Message compression
* Client heartbeat monitoring
* Session affinity for clustered deployments

## Project Structure 📁

```
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

## Roadmap 🗺️

* GraphQL API implementation ⚛️
* OAuth 2.0 social login integration 🔑
* Event-driven architecture with Spring Cloud Stream ☁️
* Kubernetes deployment manifests 🐳
* Comprehensive monitoring with Micrometer and Prometheus 📊

## Contributing 👥

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on our development process and pull request workflow.

## License 📜

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <b>Created with ❤️ by <a href="https://github.com/MrDay2Day">MrDay2Day</a></b>
</div>
