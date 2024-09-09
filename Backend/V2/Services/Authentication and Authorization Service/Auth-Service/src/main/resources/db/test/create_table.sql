-- Drop existing tables if they exist
DROP TABLE IF EXISTS UserRolesJunction;
DROP TABLE IF EXISTS RolesPermissionJunction;
DROP TABLE IF EXISTS OperationRoleJunction;
DROP TABLE IF EXISTS Operations;
DROP TABLE IF EXISTS Permissions;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Users;

-- Users Table
CREATE TABLE Users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       userID TEXT NOT NULL UNIQUE,
                       passwordHash TEXT NOT NULL,
                       userDesc TEXT,
                       created DATE,
                       updated DATE,
                       lastActivity DATE,
                       status TEXT NOT NULL
);

CREATE INDEX idx_userID ON Users (userID);

-- Roles Table
CREATE TABLE Roles (
                       id INT PRIMARY KEY,
                       name TEXT NOT NULL UNIQUE,
                       desc TEXT,
                       created DATE,
                       updated DATE
);

CREATE INDEX idx_role_name ON Roles (name);

-- Permissions Table
CREATE TABLE Permissions (
                             id INT PRIMARY KEY,
                             name TEXT NOT NULL UNIQUE,
                             desc TEXT,
                             created DATE,
                             updated DATE
);

CREATE INDEX idx_permission_name ON Permissions (name);

-- Operations Table
CREATE TABLE Operations (
                            id INT PRIMARY KEY,
                            name TEXT NOT NULL UNIQUE,
                            desc TEXT,
                            created DATE,
                            updated DATE
);

CREATE INDEX idx_operation_name ON Operations (name);

-- UserRolesJunction Table
CREATE TABLE UserRolesJunction (
                                   userID INT,
                                   roleID INT,
                                   PRIMARY KEY (userID, roleID)
);

CREATE INDEX idx_user_role_userID ON UserRolesJunction (userID);
CREATE INDEX idx_user_role_roleID ON UserRolesJunction (roleID);

-- RolesPermissionJunction Table
CREATE TABLE RolesPermissionJunction (
                                         roleID INT,
                                         permissionID INT,
                                         PRIMARY KEY (roleID, permissionID)
);

CREATE INDEX idx_role_permission_roleID ON RolesPermissionJunction (roleID);
CREATE INDEX idx_role_permission_permissionID ON RolesPermissionJunction (permissionID);

-- OperationRoleJunction Table
CREATE TABLE OperationRoleJunction (
                                       operationID INT,
                                       roleID INT,
                                       PRIMARY KEY (operationID, roleID)
);

CREATE INDEX idx_operation_role_operationID ON OperationRoleJunction (operationID);
CREATE INDEX idx_operation_role_roleID ON OperationRoleJunction (roleID);
