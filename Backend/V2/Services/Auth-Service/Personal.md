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

<details>
<summary>Override the Authentication Logic</summary>

### Overview of Security in Spring Security

Spring Security provides a comprehensive security framework for Java applications. It offers various features such as authentication, authorization, and protection against common security vulnerabilities. Here’s a brief overview of how security works in Spring Security:

1. **Security Filter Chain**:
    - Spring Security uses a chain of filters to handle security concerns. These filters process incoming requests and responses to enforce security rules such as authentication and authorization.

2. **Authentication Process**:
    - When a request comes in, Spring Security uses an `AuthenticationManager` to authenticate the user. This involves validating user credentials against a data source (like a database).

3. **Authorization Process**:
    - After successful authentication, Spring Security checks whether the user has the required permissions to access a particular resource. This involves comparing the user's roles or authorities against the required permissions.

4. **Security Context**:
    - Once authenticated, Spring Security creates a `SecurityContext` which holds the details of the authenticated user. This context is used throughout the request to enforce security policies.

5. **Custom Authentication Logic**:
    - You can override default authentication mechanisms by implementing custom filters, converters, and services to suit your application’s needs.

### Custom Authentication Flow

1. **Client sends credentials to the server.**

2. **Custom Authentication Converter**:
    - Extracts credentials from the request.
    - Converts credentials into an `Authentication` object.

3. **Custom Authentication Filter**:
    - Processes the `Authentication` object using the `AuthenticationManager`.

4. **Authentication Manager**:
    - Validates credentials against the H2 in-memory database.

5. **H2 Database**:
    - Stores test users and handles user authentication.

6. **User Details Service**:
    - Loads user details from the database.

7. **Security Context**:
    - Created for the authenticated user.

8. **Tokens** (if used):
    - Generated and sent back to the client.

9. **Subsequent Requests**:
    - Authenticated using tokens.
    - Access control is enforced.

</details>


# Image of Securiry filter
<img src="./asset/securityWebFilterChain.png">
You will need to know this to implement your custom auth logic

# Bugs and issues
## Clean maven and download dependencies if everything is valid but doesn't work
