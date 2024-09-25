# Authentication Service Design Documentation

## Table of Contents
1. [Overview](#overview)
2. [User Authentication](#user-authentication)
3. [Service Authentication](#service-authentication)
4. [Authorization Mechanism](#authorization-mechanism)

## Overview

This document outlines the design and implementation of our Authentication Service, which provides secure authentication and authorization mechanisms for both end-users and internal services.

## User Authentication

### Process Flow
1. Users are provided with a login URL corresponding to their preferred OAuth provider.
2. Upon successful authentication with the OAuth provider, users are redirected to our backend service.
3. The backend extracts the authorization code from the redirect URL's query parameters.
4. This code is used to obtain an access token from the OAuth provider.
5. The access token is then used to retrieve user information from the OAuth provider.
6. User data is either updated or added to our database as required.
7. A JWT (JSON Web Token) is generated with the following claims:
    - `sub` (subject): User ID
    - `source`: "client"
8. This JWT is returned to the user's client application.

### Usage
For all subsequent requests requiring authentication, clients must include this JWT as a bearer token in the Authorization header.

## Service Authentication

### Process Flow
1. Services create a JWT claim with the following properties:
    - `type`: "service"
    - `sub` (subject): Not included
2. All services share a common JWT signing key, distributed via a configuration server.
3. Services use this shared key to generate and sign their JWTs.

### Usage
Similar to user authentication, services must include their JWT as a bearer token in the Authorization header for inter-service communication.

## Authorization Mechanism

### Request Processing
1. An authorization filter intercepts incoming requests.
2. The filter extracts and verifies the JWT from the Authorization header.
3. The `source` claim in the JWT determines whether the request originates from a user or an internal service.

### Access Control
- User requests:
    - Superuser status is checked and recorded.
- Service requests:
    - Granted superuser access by default.

### Data Storage
Authenticated request data is stored in a request-scoped bean, including:
- User data (for user requests)
- Access level
- Request type (user/service)

### Utilization
- Self operations utilize user information from the request-scoped bean.
- Operations requiring elevated privileges check the superuser status in the bean.