

# Database Architecture

**Kuku Data Auto Pilot (KDAP)** involves the following key entities:

1. **Workspaces**
2. **Records**
3. **Task Automations** (TBD)

## 1. Workspaces
Workspaces store data in the form of **records**.

## 2. Records
Records contain large sets of entries and each has its own attributes, such as an "Inventory Record" having attributes like "ItemID", "Quantity", and "Price".

## 3. Task Automations
Details to be defined.

## Database Design

### 1. Workspaces Table
Stores information about each workspace.

**Schema:**
- `workspace_id` (Primary Key, UUID or BIGINT)
- `workspace_name` (VARCHAR, indexed)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### 2. Records Table
Stores metadata about each record, including its relationship to a workspace and the name of the dynamically created table for record data.

**Schema:**
- `record_id` (Primary Key, UUID or BIGINT)
- `workspace_id` (Foreign Key referencing `Workspaces.workspace_id`)
- `record_name` (VARCHAR, indexed)
- `table_name` (VARCHAR, stores the dynamically generated table name for the record)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### 3. Record Structures Table
Stores the schema of each record. Each entry corresponds to a record in the `Records` table.

**Schema:**
- `structure_id` (Primary Key, Foreign Key referencing `Records.record_id`)
- `structure` (JSON or TEXT, stores the record structure)
- `version` (INT, allows for schema versioning)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### 4. Dynamic Tables for Entries
Each record's entries are stored in a dynamically created table named based on the `table_name` field in the `Records` table.

**Example:**

**For an Inventory Record:**
- **Table Name:** `inventory_record_1_entries`

**Schema:**
- `entry_id` (Primary Key, UUID or BIGINT)
- `item_id` (INT)
- `item_name` (VARCHAR(255))
- `quantity` (INT)
- `price` (DECIMAL(10, 2))
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**Example Entries:**
1. **Entry 1:**
   - `entry_id`: `1`
   - `item_id`: `1001`
   - `item_name`: `"Apple"`
   - `quantity`: `50`
   - `price`: `0.99`
   - `created_at`: `2024-08-14 10:10:00`
   - `updated_at`: `2024-08-14 10:10:00`

2. **Entry 2:**
   - `entry_id`: `2`
   - `item_id`: `1002`
   - `item_name`: `"Banana"`
   - `quantity`: `75`
   - `price`: `0.79`
   - `created_at`: `2024-08-14 10:12:00`
   - `updated_at`: `2024-08-14 10:12:00`

## Operations

- **Creating a New Record:**
  1. Insert metadata into the `Records` table.
  2. Define the record structure in the `Record Structures` table.
  3. Create a dynamic table (`inventory_record_1_entries`) for storing data.

- **Adding Data to the Record:**
  Insert entries into the dynamically created table.

- **Querying Data:**
  Retrieve, update, or delete inventory items from the dynamic table.

This design provides flexibility and scalability for handling records with large datasets and supports a structured approach for managing record schemas and entries within KDAP.

