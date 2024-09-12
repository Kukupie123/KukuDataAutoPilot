
---

## Users Table

**Table Name:** `Users`

| Column         | Type     | Description                                     |
|----------------|----------|-------------------------------------------------|
| `id`           | `SERIAL` | Primary key.                                    |
| `name`         | `TEXT`   | Unique identifier for the user.                |
| `passwordHash` | `TEXT`   | Hashed password for security.                  |
| `description`  | `TEXT`   | Description of the user.                       |
| `created`      | `DATE`   | Date the user was created.                     |
| `updated`      | `DATE`   | Date the user was last updated.                |
| `lastActivity` | `DATE`   | Date of the user's last activity.              |
| `status`       | `TEXT`   | User status: `active`, `inactive`, `banned`, `pendingVerification`, `verified`, `verificationRejected`. |

**Index Usage:**

- **`name` Index:** Speeds up lookups for users based on `name`.

**Example Query:**

```sql
SELECT * FROM Users WHERE name = 'user123';
```

---

## Roles Table

**Table Name:** `Roles`

| Column        | Type     | Description                                  |
|---------------|----------|----------------------------------------------|
| `id`          | `SERIAL` | Primary key.                                 |
| `name`        | `TEXT`   | Name of the role. **Indexed for fast lookup by role name.** |
| `description` | `TEXT`   | Description of the role.                    |
| `created`     | `DATE`   | Date the role was created.                  |
| `updated`     | `DATE`   | Date the role was last updated.             |

**Index Usage:**

- **`name` Index:** Allows quick searches by role name.

**Example Query:**

```sql
SELECT * FROM Roles WHERE name = 'Admin';
```

---

## Permissions Table

**Table Name:** `Permissions`

| Column        | Type     | Description                                  |
|---------------|----------|----------------------------------------------|
| `id`          | `SERIAL` | Primary key.                                 |
| `name`        | `TEXT`   | Name of the permission. **Indexed for fast lookup by permission name.** |
| `description` | `TEXT`   | Description of the permission.              |
| `created`     | `DATE`   | Date the permission was created.            |
| `updated`     | `DATE`   | Date the permission was last updated.       |

**Index Usage:**

- **`name` Index:** Facilitates fast lookups for permissions by name.

**Example Query:**

```sql
SELECT * FROM Permissions WHERE name = 'ReadAccess';
```

---

## Operations Table

**Table Name:** `Operations`

| Column        | Type     | Description                                  |
|---------------|----------|----------------------------------------------|
| `id`          | `SERIAL` | Primary key.                                 |
| `name`        | `TEXT`   | Name of the operation. **Indexed for fast lookup by operation name.** |
| `description` | `TEXT`   | Description of the operation.               |
| `created`     | `DATE`   | Date the operation was created.             |
| `updated`     | `DATE`   | Date the operation was last updated.        |

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
| `userID`      | `INT`    | Part of composite primary key. **Indexed for fast lookup by `userID`.** |
| `roleID`      | `INT`    | Part of composite primary key. **Indexed for fast lookup by `roleID`.** |

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
| `roleID`      | `INT`    | Part of composite primary key. **Indexed for fast lookup by `roleID`.** |
| `permissionID`| `INT`    | Part of composite primary key. **Indexed for fast lookup by `permissionID`.** |

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
| `operationID` | `INT`    | Part of composite primary key. **Indexed for fast lookup by `operationID`.** |
| `roleID`      | `INT`    | Part of composite primary key. **Indexed for fast lookup by `roleID`.** |

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
