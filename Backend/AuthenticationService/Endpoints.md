# Authentication Service Endpoints


- [GET /api/authenticated](#get-apiauthenticated)
- [PUT /api/authenticated/{userID}](#put-apiauthenticateduserid)
- [GET /api/authenticated/self](#get-apiauthenticatedself)
- [PUT /api/authenticated/self](#put-apiauthenticatedself)

## Endpoints

### GET /api/authenticated

<details>
<summary>Click to expand</summary>

#### Get User Info

Get user info for any user in the system.

Payload:

```json
{
  "header": {
    "Authorization": "Bearer <JWT TOKEN>"
  },
  "path param": "userID",
  "query param": {}
}
```

Response body:

```json
{
  "msg": "message about response",
  "data": {
    "id": "userID",
    "email": "userEmail",
    "created": "created Local Date",
    "picture": "url to picture"
  }
}
```

#### Get All Users Info

Get all users' info. Only SUPER clients can do this.

Payload:

```json
{
  "header": {
    "Authorization": "Bearer <JWT TOKEN>"
  },
  "path param": "*",
  "query param": {}
}
```

Response body:

```json
{
  "msg": "message about response",
  "data": [
    {
      "id": "userID",
      "email": "userEmail",
      "created": "created Local Date",
      "picture": "url to picture"
    },
    {
      "id": "userID",
      "email": "userEmail",
      "created": "created Local Date",
      "picture": "url to picture"
    },
    "..."
  ]
}
```

</details>

### PUT /api/authenticated/{userID}

<details>
<summary>Click to expand</summary>

#### Update User Info

Update information of any user. Only SUPER clients can do this.

Payload:

```json
{
  "header": {
    "Authorization": "Bearer <JWT TOKEN>"
  },
  "body": {
    "email": "newEmail@example.com",
    "name": "New Name",
    "picture": "new_picture_url"
  },
  "path param": "userID",
  "query param": {}
}
```

Response body:

```json
{
  "msg": "message about response",
  "data": {
    "id": "userID",
    "email": "newEmail@example.com",
    "created": "created Local Date",
    "picture": "new_picture_url",
    "name": "New Name"
  }
}
```

</details>

### GET /api/authenticated/self

<details>
<summary>Click to expand</summary>

#### Get Self User Info

Returns the user info of the requesting client by extracting the authorization bearer token passed and using it to
get user info.

Payload:

```json
{
  "header": {
    "Authorization": "Bearer <JWT TOKEN>"
  },
  "path param": "",
  "query param": {}
}
```

Response body:

```json
{
  "msg": "message about response",
  "data": {
    "id": "userID",
    "email": "userEmail",
    "created": "created Local Date",
    "picture": "url to picture"
  }
}
```

</details>

### PUT /api/authenticated/self

<details>
<summary>Click to expand</summary>

#### Update Self User Info

Update information of requesting client. Client's user info is accessed by extracting claims from JWT token and using
it, which is then updated.

Payload:

```json
{
  "header": {
    "Authorization": "Bearer <JWT TOKEN>"
  },
  "body": {
    "*": "Nothing to update as of now. Every detail is taken from OAuth Resource provider such as Google."
  },
  "path param": "",
  "query param": {}
}
```

Response body:

```json
{
  "msg": "message about response",
  "data": {
    "id": "userID",
    "email": "userEmail",
    "created": "created Local Date",
    "picture": "url to picture",
    "name": "updated name"
  }
}
```

</details>

## Conclusion

<details>
<summary>Click to expand</summary>

This Authentication Service provides a robust system for handling both client and internal authentication and
authorization. It uses JWT tokens for secure communication and supports different access levels (SELF and SUPER) to
control permissions. The service integrates with OAuth providers like Google for client authentication and provides a
comprehensive set of endpoints for managing user information.

Key features:

1. Unified endpoints for authenticated requests under `/api/authenticated`
2. Separate endpoint for self-related operations under `/api/authenticated/self`
3. JWT token-based authentication
4. OAuth integration for client authentication
5. SELF and SUPER access levels
6. Comprehensive user management capabilities

For any further development or integration, please refer to the specific endpoint documentation provided above. Always
ensure to follow security best practices when handling authentication and user data.

</details>