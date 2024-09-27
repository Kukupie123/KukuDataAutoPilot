# Databox Service Endpoints

## Base URL
```
/api/authenticated/databox
```
## GET Endpoints

### 1. Get Databox(es)
- **Endpoint:** `/`
- **Method:** GET
- **Query Parameters:**
  - `boxid` (optional): If provided, returns the specific DataBox.
  - `userid` (optional): If provided, returns all DataBoxes of that user.
  - `skip` (optional, default: 0): The number of records to skip for pagination.
  - `limit` (optional, default: 10): The maximum number of records to return.
- **Description:** Retrieves DataBox(es) based on query parameters. If no specific parameters are provided, returns all DataBoxes with pagination.

## POST Endpoints

### 1. Add Databox
- **Endpoint:** `/`
- **Method:** POST
- **Body:** DataBox object to be created
- **Description:** Adds a new DataBox.

## PUT Endpoints

### 1. Update Databox
- **Endpoint:** `/`
- **Method:** PUT
- **Body:** DataBox object containing updated data
- **Description:** Updates an existing DataBox.

## DELETE Endpoints

### 1. Delete Specific Databox
- **Endpoint:** `/{id}`
- **Method:** DELETE
- **Path Variables:**
  - `id`: The ID of the DataBox to be deleted
- **Description:** Deletes a specific DataBox by its ID.
