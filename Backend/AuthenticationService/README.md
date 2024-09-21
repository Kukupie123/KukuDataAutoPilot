# Authentication Service

Authentication Service takes care of authentication and authorization and user data. It provides OAuth2 based login and
uses JWT Token for Authorization.

## Technologies and Concepts used

1. Spring Webflux :- For reactive non-blocking operation since it's going to receive a lot of traffic.
2. Spring Security & OAuth2 :- Allows to easily integrate and modify custom security and OAuth resource access.
3. JWT Tokens :-
4. R2DBC & Postgresql :- Reactive library with postgresql driver allows us to operate with postgresql database server in
   non-blocking manner.
5. Message Broker (RabbitMQ) :- Allows asynchronous operations between services and helps to establish a solid
   Publisher-Consumer model.
6. Caching Server (Redis) :- Frequently accessed data such as entity from database are cached in a caching server. The
   reason behind a caching server instead of app level caching is for scenario where we will have multiple instances
   running. This will require a central endpoint to access the caches.
7. Service Discover (Future) :- In the near future once the app is mostly done, service discovery will be introduced to help with service discovery, load balancing when we introduce autoscaling.

## Technology Descriptions and use case

### Authentication and Authorization
Security is one of the biggest concern when it comes to applications. This one is no different. 
Providing a convenient way for users to signup/login while being secure is a challenge itself.
We are going to go with OAuth2 based login/signup as it's very easy to user as a user. 
Here are the steps we take for authentication and authorization
#### Authentication Flow
1. Users are presented with a url to login. The URL will contain clientID and other details required by the provider.
2. Users then grant permission to it's resources and the provider will redirect to the configured path. It is important that the redirect path is on the backend endpoint. This is because the redirect URL usually contains sensitive information.
3. Once the provider redirects to the configured URL(in our case a public endpoint in this service) "authorization code" is extracted and used to get an access token.
4. Access token is then used to get resources from the provider's resource server.
5. In our case we get back user data and then proceed to store it in our database.
6. If the user doesn't exist yet its as simple as inserting a new record.
7. However, if the user already exists, we get the user from the database or cache (hopefully) and compare it against the user data we got from the provider's resource server.
8. If the user data is outdated in our system we update the user's data in the database and publish an event to the message broker.
9. If the user's data is valid we do not update the data.
10. Once these things are done we generate a JWT token with its subject set to UID of the user and return it as response.
#### Authorization Flow
1. Clients have to provide the token sent during OAuth2 authentication as a authorization header in form of a "Bearer ...." token.
2. When it hits an endpoint, a custom filter attempts to extract claims from the token.
3. If successful it is going to create a "PreAuthenticated" authentication object which has not been authenticated yet where it's credentials are set to the subject(UID of the user) of the claim.
4. The chain is then forwarded to a custom Authentication Manager which uses the credential to get the user from the database or cache (hopefully).
5. If it can retrieve the user successfully then the user has been authorized to move forward.
6. We do not have any user for roles yet as all users have the same privilege. In near future when we want to have a superuser with complete access we will introduce the concepts of roles.

### Publishers and Consumers (RabbitMQ)
We use RabbitMQ to publish "UserUpdated" event.
<br>
Event Driven Architecture is crucial for applications to be decoupled and asynchronous. Message broker such as RabbitMQ helps us achieve this by allowing us to publish a message which can be listened by a consumer.
It can use several protocol for forwarding the message such as AMQP, TCP, etc.
RabbitMQ uses queue based storage mechanism. The other type of message broker is log based mechanism.
It acts as a central hub for sending messages across services instead of having to send the messages directly.
#### Exchange
In rabbitMQ, Exchanges define the type of message we want to send. They are 
1. Direct Exchange :- In this exchange message is sent to all queues who are bound whose routing key match exactly. More on routing key later down the line.
2. Topic Exchange :- Topic exchange allows message to be sent to queues matching a certain set of wildcard criteria for routing key.
3. Fanout :- Sends the message to all queues with no regards to routing key
4. Header exchange :- Uses header attributes to determine where to send the queues.
#### Queue
Queue is where the message is actually stored and consumed from by workers/consumers. It follows the 'First in First out'. This basically means that message that came first will be consumed first.
It has several configurations that can be adjusted as our need such as "Max no. of queues", "durable or non durable (Should message persist in disk or not)", message TTL, etc.
#### Routing
Routing key is used by several types of exchanges to determine which queues to send the message to. It 'binds' an exchange to a queue.
<br><br>

### Redis Cache Server
We use Redis cache server to store user accessed from the database. We have to make sure that we invalidate the user data stored in cache when user has been updated in database.
