Certainly! Here's a detailed Markdown representation of your database schema with explanations of index and composite index usage for each table and example queries.

---

# Database Schema

## Users Table

**Table Name:** `Users`

| Column        | Type     | Description                                     |
|---------------|----------|-------------------------------------------------|
| `id`           | `int`    | Primary key. **Note:** Index for `userID`.      |
| `userID`       | `text`   | Unique identifier for the user.                |
| `passwordHash` | `text`   | Hashed password for security.                  |
| `userDesc`     | `text`   | Description of the user.                       |
| `created`      | `date`   | Date the user was created.                     |
| `updated`      | `date`   | Date the user was last updated.                |
| `lastActivity` | `date`   | Date of the user's last activity.              |
| `status`       | `enum`   | User status: `active`, `inactive`, `banned`, `pendingVerification`, `verified`, `verificationRejected`. |

**Index Usage:**

- **`userID` Index:** Speeds up lookups for users based on `userID`.

**Example Query:**

```sql
SELECT * FROM Users WHERE userID = 'user123';
```

---

## Roles Table

**Table Name:** `Roles`

| Column        | Type     | Description                                 |
|---------------|----------|---------------------------------------------|
| `id`           | `int`    | Primary key.                                |
| `name`         | `text`   | Name of the role. **Note:** Indexed for fast lookup by role name. |
| `desc`         | `text`   | Description of the role.                   |
| `created`      | `date`   | Date the role was created.                 |
| `updated`      | `date`   | Date the role was last updated.            |

**Index Usage:**

- **`name` Index:** Allows quick searches by role name.

**Example Query:**

```sql
SELECT * FROM Roles WHERE name = 'Admin';
```

---

## Permissions Table

**Table Name:** `Permissions`

| Column        | Type     | Description                                 |
|---------------|----------|---------------------------------------------|
| `id`           | `int`    | Primary key.                                |
| `name`         | `text`   | Name of the permission. **Note:** Indexed for fast lookup by permission name. |
| `desc`         | `text`   | Description of the permission.             |
| `created`      | `date`   | Date the permission was created.           |
| `updated`      | `date`   | Date the permission was last updated.      |

**Index Usage:**

- **`name` Index:** Facilitates fast lookups for permissions by name.

**Example Query:**

```sql
SELECT * FROM Permissions WHERE name = 'ReadAccess';
```

---

## Operations Table

**Table Name:** `Operations`

| Column        | Type     | Description                                 |
|---------------|----------|---------------------------------------------|
| `id`           | `int`    | Primary key.                                |
| `name`         | `text`   | Name of the operation. **Note:** Indexed for fast lookup by operation name. |
| `desc`         | `text`   | Description of the operation.              |
| `created`      | `date`   | Date the operation was created.            |
| `updated`      | `date`   | Date the operation was last updated.       |

**Index Usage:**

- **`name` Index:** Enhances performance for finding operations by name.

**Example Query:**

```sql
SELECT * FROM Operations WHERE name = 'CreateRecord';
```

---

## UserRolesJunction Table

**Table Name:** `UserRolesJunction`

| Column        | Type     | Description                                         |
|---------------|----------|-----------------------------------------------------|
| `userID`      | `int`    | Composite primary key. **Note:** Indexed for fast lookup by `userID`. |
| `roleID`      | `int`    | Composite primary key. **Note:** Indexed for fast lookup by `roleID`. |

**Index Usage:**

- **Composite Index (`userID`, `roleID`):** Optimizes queries that filter by both `userID` and `roleID`.

**Example Queries:**

1. **Find Roles by `userID`:**

   ```sql
   SELECT roleID FROM UserRolesJunction WHERE userID = 1;
   ```

2. **Find Users by `roleID`:**

   ```sql
   SELECT userID FROM UserRolesJunction WHERE roleID = 2;
   ```

---

## RolesPermissionJunction Table

**Table Name:** `RolesPermissionJunction`

| Column        | Type     | Description                                         |
|---------------|----------|-----------------------------------------------------|
| `roleID`      | `int`    | Composite primary key. **Note:** Indexed for fast lookup by `roleID`. |
| `permissionID`| `int`    | Composite primary key. **Note:** Indexed for fast lookup by `permissionID`. |

**Index Usage:**

- **Composite Index (`roleID`, `permissionID`):** Improves performance for queries involving both `roleID` and `permissionID`.

**Example Queries:**

1. **Find Permissions by `roleID`:**

   ```sql
   SELECT permissionID FROM RolesPermissionJunction WHERE roleID = 3;
   ```

2. **Find Roles by `permissionID`:**

   ```sql
   SELECT roleID FROM RolesPermissionJunction WHERE permissionID = 4;
   ```

---

## OperationRoleJunction Table

**Table Name:** `OperationRoleJunction`

| Column        | Type     | Description                                         |
|---------------|----------|-----------------------------------------------------|
| `operationID` | `int`    | Composite primary key. **Note:** Indexed for fast lookup by `operationID`. |
| `roleID`      | `int`    | Composite primary key. **Note:** Indexed for fast lookup by `roleID`. |

**Index Usage:**

- **Composite Index (`operationID`, `roleID`):** Optimizes queries filtering by both `operationID` and `roleID`.

**Example Queries:**

1. **Find Roles by `operationID`:**

   ```sql
   SELECT roleID FROM OperationRoleJunction WHERE operationID = 5;
   ```

2. **Find Operations by `roleID`:**

   ```sql
   SELECT operationID FROM OperationRoleJunction WHERE roleID = 6;
   ```

---
