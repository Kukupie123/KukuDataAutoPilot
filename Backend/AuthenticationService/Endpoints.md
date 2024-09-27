

---

# Authentication Service Endpoints

- [GET /api/authenticated](#get-apiauthenticated)
- [PUT /api/authenticated/{userID}](#put-apiauthenticateduserid)
- [DELETE /api/authenticated/{userID}](#delete-apiauthenticateduserid)
- [GET /api/authenticated/self](#get-apiauthenticatedself)
- [PUT /api/authenticated/self](#put-apiauthenticatedself)
- [DELETE /api/authenticated/self](#delete-apiauthenticatedself)

## Endpoints

### GET /api/authenticated

<details>
<summary>Click to expand</summary>

#### Get Self User Info (No Path Param)

Retrieves the information of the authenticated user (i.e., the user making the request).

**Request:**

- Method: `GET`
- Path: `/api/authenticated`
- Auth Header: Bearer `<JWT TOKEN>`

**Response:**

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

#### Get All Users Info (`/*` Path Param)

Retrieves a list of all users' information. Only accessible by users with SUPER access.

**Request:**

- Method: `GET`
- Path: `/api/authenticated/*`
- Auth Header: Bearer `<JWT TOKEN>`
- Query params:
    - `skip`: Number of records to skip (optional, defaults to `0`)
    - `limit`: Maximum number of records to return (optional, defaults to `10`)

**Response:**

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
    ...
  ]
}
```

#### Get Specific User Info (User ID Path Param)

Retrieves information about a specific user by their ID.

**Request:**

- Method: `GET`
- Path: `/api/authenticated/{userID}`
- Auth Header: Bearer `<JWT TOKEN>`

**Response:**

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

### PUT /api/authenticated/{userID}

<details>
<summary>Click to expand</summary>

#### Update User Info

Updates the information of any user. This operation can only be performed by users with SUPER access.

**Request:**

- Method: `PUT`
- Path: `/api/authenticated/{userID}`
- Auth Header: Bearer `<JWT TOKEN>`
- Body:

```json
{
  "email": "newEmail@example.com",
  "name": "New Name",
  "picture": "new_picture_url"
}
```

**Response:**

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

### DELETE /api/authenticated/{userID}

<details>
<summary>Click to expand</summary>

#### Delete User

Deletes a user by their ID. This operation can only be performed by users with SUPER access.

**Request:**

- Method: `DELETE`
- Path: `/api/authenticated/{userID}`
- Auth Header: Bearer `<JWT TOKEN>`

**Response:**

```json
{
  "msg": "message about response",
  "data": true
}
```

</details>

### GET /api/authenticated/self

<details>
<summary>Click to expand</summary>

#### Get Self User Info

Retrieves the information of the authenticated user making the request.

**Request:**

- Method: `GET`
- Path: `/api/authenticated/self`
- Auth Header: Bearer `<JWT TOKEN>`

**Response:**

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

Updates the information of the authenticated user making the request.

**Request:**

- Method: `PUT`
- Path: `/api/authenticated/self`
- Auth Header: Bearer `<JWT TOKEN>`
- Body:

```json
{
  "email": "newEmail@example.com",
  "name": "New Name",
  "picture": "new_picture_url"
}
```

**Response:**

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

### DELETE /api/authenticated/self

<details>
<summary>Click to expand</summary>

#### Delete Self User

Deletes the account of the authenticated user making the request.

**Request:**

- Method: `DELETE`
- Path: `/api/authenticated/self`
- Auth Header: Bearer `<JWT TOKEN>`

**Response:**

```json
{
  "msg": "message about response",
  "data": true
}
```

</details>

## Conclusion

<details>
<summary>Click to expand</summary>

This Authentication Service provides a robust system for handling both client and internal authentication and authorization. It uses JWT tokens for secure communication and supports different access levels (SELF and SUPER) to control permissions. The service integrates with OAuth providers like Google for client authentication and provides a comprehensive set of endpoints for managing user information.

Key features:

1. Unified endpoints for authenticated requests under `/api/authenticated`
2. Separate endpoint for self-related operations under `/api/authenticated/self`
3. JWT token-based authentication
4. OAuth integration for client authentication
5. SELF and SUPER access levels
6. Comprehensive user management capabilities

For any further development or integration, please refer to the specific endpoint documentation provided above. Always ensure to follow security best practices when handling authentication and user data.

</details>

--- 

This update reflects the new changes in the controller for the `/api/authenticated`, `/api/authenticated/*`, and `/api/authenticated/{id}` routes, as well as the `/self` endpoints.