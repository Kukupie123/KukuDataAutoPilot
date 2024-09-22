# Database Design

## Requirements
The core operations that we will be performing are:
- Creating data stores with defined fields
- Adding entries to data stores
- Retrieving entries from data stores

### Frequency of Operations
1. **Getting Entries**: High priority
2. **Adding Entries**: High priority
3. **Editing Data Store Fields**: Medium priority
4. **Creating Data Stores**: Low priority

## Database Type Selection
We have multiple database options: Relational, Document, and Columnar. Below is an analysis of each type based on our requirements.

### 1. Relational Database
**Characteristics**:
- Rigid structure that may complicate modifications to existing data stores.
- Tables represent data stores, fields, and entries.

**Table Structures**:
- **Data Store Table**

  | Column   | Type      |
    |----------|-----------|
  | StoreID  | VARCHAR   |
  | Name     | VARCHAR   |
  | Created  | TIMESTAMP |
  | Updated  | TIMESTAMP |

- **Data Store Fields Table**

  | Column   | Type      |
    |----------|-----------|
  | FieldID  | VARCHAR   |
  | Name     | VARCHAR   |
  | StoreID  | VARCHAR   |
  | Type     | VARCHAR   |
  | Optional | BOOLEAN   |
  | Etc.     | VARCHAR   |

- **Data Entries Table**

  | Column   | Type      |
    |----------|-----------|
  | EntryID  | VARCHAR   |
  | FieldID  | VARCHAR   | (Composite PK)
  | Value    | TEXT      |

**Considerations**:
- Queries require reading from multiple tables, which can complicate operations.
- Indexing is possible for faster operations, making reads faster.
- Solid ACID compliance for transactions, but not ideal for horizontal scaling.

---

### 2. Document Database
**Characteristics**:
- Each element can represent its own data store.
- Flexible structure for easy modification and scalability.

**Data Structure**:
- **Data Store Example**:
    ```json
    {
      "_id": "store1",
      "name": "My First Data Store",
      "created": "2023-09-22",
      "updated": "2023-09-22",
      "fields": [
        {
          "fieldId": "field1",
          "name": "First Name",
          "type": "string",
          "optional": false
        },
        {
          "fieldId": "field2",
          "name": "Age",
          "type": "integer",
          "optional": true
        }
      ]
    }
    ```

- **Entry Example**:
    ```json
    {
      "_id": "entry1",
      "storeId": "store1",
      "values": {
        "field1": "John Doe",
        "field2": 30
      },
      "created": "2023-09-22",
      "updated": "2023-09-22"
    }
    ```

**Considerations**:
- Highly scalable horizontally.
- Easier to manage updates and modifications.
- Supports transactions in most document databases.
- Simplified queries based on requirements.

---

### 3. Columnar Database
**Characteristics**:
- Stores data in columns instead of rows.
- Optimized for queries that involve multiple columns.

**Table Structure**:
- **Data Store Table**

  | StoreID | Name        | Created      | Updated      |
    |---------|-------------|--------------|--------------|
  | store1  | My First    | 2023-09-22   | 2023-09-22   |

- **Field Table**

  | FieldID | StoreID | Name        | Type    | Optional |
    |---------|---------|-------------|---------|----------|
  | field1  | store1  | First Name  | string  | false    |
  | field2  | store1  | Age         | integer | true     |

- **Entries Table**

  | EntryID | StoreID | field1    | field2 |
    |---------|---------|-----------|--------|
  | entry1  | store1  | John Doe  | 30     |

**Considerations**:
- Good for queries that require data from specific columns and then grouping them with other few columns. It's easier to grasp if you think of each column as "Table" of a Relational Database.
- Not ideal for scenarios like “getting entries whose store ID is 123” as it requires accessing multiple columns.
- Updates can be complex as multiple columns may need to be modified.

---

## Final Choice
We will proceed with a **Document Database** due to the following reasons:
- Horizontal scalability.
- Simplified data structure for managing stores and entries.
- Support for transactions in most databases.
- Easier querying and modification processes.

### Collections
We will have two collections:
1. **Stores**: Information about data stores.
2. **Entries**: All entries linked to their respective stores.

### Example Collections
- **Stores Collection**:
    ```json
    {
      "_id": "store1",
      "name": "My First Data Store",
      "created": "2023-09-22",
      "updated": "2023-09-22",
      "fields": [
        {
          "fieldId": "field1",
          "name": "First Name",
          "type": "string",
          "optional": false
        },
        {
          "fieldId": "field2",
          "name": "Age",
          "type": "integer",
          "optional": true
        }
      ]
    }
    ```

- **Entries Collection**:
    ```json
    {
      "_id": "entry1",
      "storeId": "store1",
      "values": {
        "field1": "John Doe",
        "field2": 30
      },
      "created": "2023-09-22",
      "updated": "2023-09-22"
    }
    ```

## Data Store Relationship Specifier
We will implement a mechanism to specify relationships between entities using a series of questions:
- Can one owner have many cars? **Yes**
- Can one car have many owners? **No**

This indicates a **one-to-many relationship**.

---
