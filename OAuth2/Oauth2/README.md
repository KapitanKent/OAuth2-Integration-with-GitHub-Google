# OAuth2 Demo

This Spring Boot application demonstrates OAuth2 login with Google and GitHub, user provisioning, and a minimal profile module.

## Setup

1. Copy the example properties and fill in your OAuth credentials:

```powershell
Copy-Item -Path src\main\resources\application.properties.example -Destination src\main\resources\application.properties
# Then edit src\main\resources\application.properties and replace the placeholders
```

Example properties (replace placeholders):

```properties
spring.security.oauth2.client.registration.github.client-id=your-github-client-id
spring.security.oauth2.client.registration.github.client-secret=your-github-client-secret
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret

# H2 Database (development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

2. Run the app (PowerShell):

```powershell
mvn -f Oauth2\pom.xml spring-boot:run
# or build the jar
mvn -f Oauth2\pom.xml -DskipTests package
java -jar Oauth2\target\Oauth2-0.0.1-SNAPSHOT.jar
```

3. Open http://localhost:8080 and use the login buttons on the home page.

## Notes

- Uses H2 in-memory DB for development. Swap to MySQL/Postgres by updating datasource properties.
- Session-based security with Spring Security and `spring-boot-starter-oauth2-client`.

## Architecture

See `docs/architecture.md` for a short architecture description and a PlantUML diagram in `diagrams/architecture.puml`.

## Tests

- Run `mvn -f Oauth2\pom.xml test` to execute tests (none provided by default).
