### Auth Service

This microservice handles role-based authentication with a focus on scalability and efficiency. Here’s a detailed overview of its features and setup:

#### Overview

The Auth Service provides:
- **Username/Password Authentication:** Secure login using standard credentials.
- **Third-Party Authentication:** Integration with Google and Git for third-party login options.

#### Key Features

- **Non-Blocking and Scalable:** Built with Spring WebFlux for non-blocking operations, ensuring high scalability and efficient handling of a large number of requests.

- **Event-Driven Architecture:** Utilizes an event-driven design to respond effectively to application events and changes.

- **Service Discovery:** Designed with service discovery capabilities to ensure efficient location and utilization within a distributed system.

- **Independent Scaling:** Architecture allows for independent scaling of application and database containers, optimizing performance and resource usage.

- **Caching:** Implements caching at both the application and service levels to reduce latency and improve response times.
