
---

### Database Setup and Initialization Flow for the Test Profile

1. **Database Configuration**

   We configure the database connection specifically for the "test" profile. This is achieved by defining a `ConnectionFactory` that connects to an H2 database for testing purposes.

   ```java
   @Bean
   public ConnectionFactory testConnectionFactory() {
       // Configure H2 database connection options
   }
   ```

    - **Purpose:** This bean sets up the H2 database connection parameters such as driver, database path, username, and password.

2. **Database Schema Initialization**

   After setting up the connection, we need to initialize the database schema. This is done using a `ConnectionFactoryInitializer` which executes SQL scripts to create tables and schema components.

   ```java
   @Bean
   public ConnectionFactoryInitializer initializeDatabase() {
       // Initialize the database schema
   }
   ```

    - **Purpose:** This bean runs SQL scripts to set up the initial database schema, ensuring that the database is ready for testing with the required structure.

3. **Database Schema Setup**

   The `ConnectionFactoryInitializer` reads the schema creation script (`create_table.sql`) from the classpath and executes it against the H2 database. This script typically includes SQL statements to create tables and indexes necessary for the application.

   ```java
   Resource resource = new ClassPathResource("db/test/create_table.sql");
   ```

    - **Purpose:** To ensure that the test database has the correct schema for running tests, this step involves setting up the tables and other schema-related elements.

4. **Test Data Initialization**

   After the schema is set up, we need to insert some initial data into the database. This is done by a component that listens for the application context refresh event and performs the data insertion.

   ```java
   @Override
   public void onApplicationEvent(ContextRefreshedEvent event) {
       // Create and insert test data
   }
   ```

    - **Purpose:** This component initializes the test database with default data, such as a root user, to ensure that the database has necessary entries for testing.

5. **Repository Interface**

   We define a repository interface (`IUserRepository`) for interacting with the `UserDbLevel` entity. This interface provides methods to query the user data from the database.

   ```java
   public interface IUserRepository {
       // Methods to retrieve user data
   }
   ```

    - **Purpose:** This interface abstracts the data access layer, providing methods to perform CRUD operations on user data.

6. **Repository Implementation**

   The `UserRepositoryTest` class implements the `IUserRepository` interface. It uses the `R2dbcEntityTemplate` to perform database operations such as fetching users by ID or userID.

   ```java
   @Override
   public Mono<UserDbLevel> getUserById(int id) {
       // Fetch user by ID
   }

   @Override
   public Flux<UserDbLevel> getUsersByUserId(String userId) {
       // Fetch users by userID
   }
   ```

    - **Purpose:** This implementation interacts with the database using reactive programming principles, providing concrete methods to query and manage user data.

---
