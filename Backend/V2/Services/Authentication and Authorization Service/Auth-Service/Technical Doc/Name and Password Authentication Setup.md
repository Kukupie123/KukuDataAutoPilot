
---

### Authentication Flow in Our Setup

1. **User Request**

   When a user attempts to access a secured endpoint, they provide their credentials (username and password). The request is handled by Spring Security.

2. **Security Configuration**

   In our security configuration, we define which endpoints are secured and which are publicly accessible. For example:

   ```java
   @Bean
   public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
       return http.authorizeExchange()
                   .pathMatchers("/get", "/add", "/h2").permitAll()
                   .anyExchange().authenticated()
                   .authenticationManager(authenticationManager)
                   .formLogin(Customizer.withDefaults())
                   .build();
   }
   ```

   This setup ensures that all requests are authenticated unless specified otherwise.

3. **Authentication Request Handling**

   When a request with credentials is made, Spring Security uses our `CustomAuthenticationManager` to process the authentication.

4. **Custom Authentication Manager**

   The `CustomAuthenticationManager` is responsible for validating the user's credentials. It interacts with the `CustomUserDetailsService` to retrieve user details.

   ```java
   @Component
   public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
       @Autowired
       private CustomUserDetailsService customUserDetailsService;

       @Override
       public Mono<Authentication> authenticate(Authentication authentication) {
           // Logic to authenticate the user
       }
   }
   ```

    - **Step 1:** The manager receives an `Authentication` object containing the provided username and password.
    - **Step 2:** It uses the `CustomUserDetailsService` to fetch the user's details from the repository.

5. **User Details Service**

   The `CustomUserDetailsService` fetches user information from the database and maps it to our `CustomUserDetails` object.

   ```java
   @Component
   public class CustomUserDetailsService implements ReactiveUserDetailsService {
       @Autowired
       private IUserRepository userRepository;

       @Override
       public Mono<UserDetails> findByUsername(String username) {
           // Logic to retrieve user details
       }
   }
   ```

    - **Step 1:** The service queries the database for the user with the given username.
    - **Step 2:** It returns a `CustomUserDetails` object containing the user’s information if found.

6. **Credential Verification**

   Back in the `CustomAuthenticationManager`, it compares the provided password with the one stored in the `CustomUserDetails` object.

   ```java
   @Override
   public Mono<Authentication> authenticate(Authentication authentication) {
       String password = authentication.getCredentials().toString();
       String username = authentication.getName();
       return customUserDetailsService.findByUsername(username)
               .flatMap(userDetails -> {
                   if (userDetails.getPassword().equals(password)) {
                       // Authentication successful
                   } else {
                       // Authentication failed
                   }
               })
               .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")));
   }
   ```

    - **Step 1:** If the passwords match, it creates an `Authentication` object representing the authenticated user.
    - **Step 2:** If they do not match or if the user is not found, it returns an error.

7. **Access Granted**

   If authentication is successful, Spring Security grants access to the requested endpoint. The user is considered authenticated and authorized based on their roles and permissions.

8. **Access Denied**

   If authentication fails, Spring Security handles the error according to the configured authentication failure handler, typically returning an error response to the user.

---
