### Components in Our Project

In our project, we use `@Component` to define classes that serve specific roles in the application. Here’s a detailed explanation of how and why we use `@Component`:

1. **Definition of `@Component`**:
    - The `@Component` annotation in Spring is used to mark a class as a Spring-managed component. This means that Spring will automatically detect these classes and create instances of them during the component scanning phase of the Spring container’s startup.

2. **Purpose of Components**:
    - Components are used to define beans that are automatically managed by the Spring framework. They can be used for various purposes, such as handling authentication, interacting with the database, or performing application-level initialization tasks.

3. **Types of Components**:
    - **Authentication Components**: For example, `CustomAuthenticationManager` and `CustomUserDetailsService` are components related to user authentication and details retrieval. These components help us implement our own logic for handling user authentication and fetching user details.
    - **Database Initialization Components**: For example, `TestDatabaseInitializer` is a component used to initialize the database with necessary schema and initial data. It performs tasks like creating tables, inserting initial records, and setting up test data.

4. **Usage of Components**:
    - **Injecting Dependencies**: Components can be injected into other Spring-managed beans using `@Autowired` or constructor injection. This allows for dependency management and ensures that all necessary services are available where needed.
    - **Context Initialization**: Some components are specifically designed to execute logic after the Spring application context is fully initialized. For instance, `TestDatabaseInitializer` runs after the application context is refreshed to set up the test database environment.

By using `@Component`, we ensure that our classes are properly managed by Spring, allowing us to leverage Spring’s dependency injection and lifecycle management features.
