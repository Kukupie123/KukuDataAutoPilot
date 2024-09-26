# Databox Service Endpoints

## Base URL
```
api/authenticated/db
```

## Access Rules
- Users can always access and modify their own databoxes, even if they are not admins.
- Admin users have access to all databoxes and can perform operations on behalf of other users.

## GET Endpoints

### 1. Get All Databoxes (Admin Only)
- **Endpoint:** `/`
- **Method:** GET
- **Query Parameters:**
  - `skip`
  - `limit`
- **Description:** Retrieves databoxes of all users. Requires admin rights.

### 2. Get Databoxes of Specified User (Admin Only)
- **Endpoint:** `/`
- **Method:** GET
- **Query Parameters:**
  - `skip`
  - `limit`
  - `userid`
- **Description:** Retrieves databoxes of a specified user. Requires admin rights.

### 3. Get Specific Databox
- **Endpoint:** `/{dbID}`
- **Method:** GET
- **Description:** Retrieves a specific databox. Users can access their own databoxes. Admins can access any databox.

## PUT Endpoints

### 1. Update Databox
- **Endpoint:** `/`
- **Method:** PUT
- **Body:** `{updated databox json}`
- **Description:** Updates a databox. Users can update their own databoxes. Admins can update any databox.

## POST Endpoints

### 1. Add Databox
- **Endpoint:** `/`
- **Method:** POST
- **Body:** `{databox to add}`
- **Description:** Adds a new databox. Users can add databoxes for themselves. Admins can add databoxes for any user.

## DELETE Endpoints

### 1. Delete All Databoxes (Admin Only)
- **Endpoint:** `/`
- **Method:** DELETE
- **Description:** Deletes all databoxes. Requires admin rights.

### 2. Delete All Databoxes of a User (Admin Only)
- **Endpoint:** `/`
- **Method:** DELETE
- **Query Parameters:**
  - `user=userID`
- **Description:** Deletes all databoxes of a specified user. Requires admin rights.

### 3. Delete Specific Databox
- **Endpoint:** `/{id}`
- **Method:** DELETE
- **Description:** Deletes a specific databox. Users can delete their own databoxes. Admins can delete any databox.