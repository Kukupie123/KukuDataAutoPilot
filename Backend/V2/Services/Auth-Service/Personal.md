<details>
<summary>Spring Configuration Methods</summary>

**1. Configuration via Java Beans**
- Define configuration using Java classes annotated with `@Configuration` and `@Bean`.
- Allows you to create and configure beans programmatically within your application context.

**2. Configuration via Properties Files**
- Specify configuration settings in external properties files (e.g., `application.properties` or `application.yml`).
- Define application-specific settings that can be injected into Spring beans.

**3. Profile-Based Configuration**
- Use profiles to provide different configurations for different environments.
- Define beans or configuration settings specific to a profile using annotations like `@Profile` or profile-specific property files (e.g., `application-dev.properties`).

**4. Configuration through Environment Variables**
- Configure Spring applications using environment variables.
- Environment variables can override properties defined in `application.properties` or `application.yml`.

**5. Conditional Configuration**
- Use annotations like `@ConditionalOnProperty`, `@ConditionalOnClass`, or `@ConditionalOnBean` to conditionally include or exclude configuration based on certain conditions.

**6. Dynamic Configuration**
- Use the `ConfigurableApplicationContext` interface to register and unregister beans dynamically.
- Useful for changing configuration at runtime.

**7. Multiple Configuration Sources**
- Combine multiple configuration sources, such as properties files and Java-based configuration.
- Spring will merge these configurations based on their precedence.

**8. Configuration via Spring Boot Starter Dependencies**
- Use starter dependencies that come with predefined configurations and settings.
- Simplifies the setup of common scenarios and reduces the need for manual configuration.

</details>


# Bugs and issues
## Clean maven and download dependencies if everything is valid but doesn't work
