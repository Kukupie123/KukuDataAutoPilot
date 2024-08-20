# Workspace and Record Management System

## Overview

This document describes the design for a system managing workspaces and records, including a custom indexing strategy to optimize query performance. The design is implemented using a NoSQL database with a focus on efficiency and scalability.

## Tables

### 1. Workspace Table

- **`id`**: Unique identifier for the workspace (e.g., UUID or a generated ID).
- **`name`**: Name of the workspace.
- **`created`**: Date the workspace was created.
- **`updated`**: Date the workspace was last updated.
- **`desc`**: Description of the workspace.

### 2. Record Table

- **`id`**: Unique identifier for the record.
- **`name`**: Name of the record.
- **`created`**: Date the record was created.
- **`updated`**: Date the record was last updated.
- **`desc`**: Description of the record.
- **`attributes`**: Defines the structure or schema of the record, potentially stored as JSON or XML.

## Indexing Strategy

To enhance query performance, custom indexing tables are used to manage the relationships between workspaces and records.

### 1. `ws_rec` Table

- **Purpose**: This table facilitates fast lookups to determine which records are associated with a given workspace.
- **Structure**:
  - **`id`**: A concatenated string of `workspaceID` and `recordID` (e.g., `workspaceID_recordID`).
- **Indexing**:
  - The `id` column is indexed to optimize queries.

### 2. `rec_ws` Table

- **Purpose**: This table enables quick lookups to find all workspaces associated with a specific record.
- **Structure**:
  - **`id`**: A concatenated string of `recordID` and `workspaceID` (e.g., `recordID_workspaceID`).
- **Indexing**:
  - The `id` column is indexed to enhance query performance.

## Query Examples

### Get Workspaces That Use Record X

To find all workspaces associated with a specific record:
- **Find Entries**: Query the `rec_ws` table for entries where `recordID` is equal to `X`.
- **Extract Data**: Retrieve the associated `workspaceID` values from these entries.

**Pseudo-code Example**:
```
Find entries in rec_ws where recordID equals 'X'
Extract workspaceID values from these entries
```

### Get Records That Are Used in Workspace X

To find all records associated with a specific workspace:
- **Find Entries**: Query the `ws_rec` table for entries where `workspaceID` is equal to `X`.
- **Extract Data**: Retrieve the associated `recordID` values from these entries.

**Pseudo-code Example**:
```
Find entries in ws_rec where workspaceID equals 'X'
Extract recordID values from these entries
```

## Data Consistency

To maintain consistency between the main tables and the indexing tables:

- **Additions**: When adding a new workspace or record, ensure that corresponding entries are created in both indexing tables.
- **Deletions**: When removing a workspace or record, ensure that all related entries are deleted from the indexing tables.
- **Updates**: Any changes to the relationships between workspaces and records should be reflected in the indexing tables.

<br>
This design provides a robust and efficient method for managing and querying relationships between workspaces and records. By using custom indexing tables, we achieve quick lookups and maintain the scalability of the system.
