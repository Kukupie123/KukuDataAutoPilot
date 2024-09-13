-- Users Table
CREATE TABLE Users (
                       id SERIAL PRIMARY KEY,
                       name TEXT UNIQUE NOT NULL,
                       passwordHash TEXT NOT NULL,
                       description TEXT,
                       created DATE NOT NULL DEFAULT CURRENT_DATE,
                       updated DATE NOT NULL DEFAULT CURRENT_DATE,
                       lastActivity DATE,
                       status TEXT NOT NULL
);

CREATE INDEX idx_users_name ON Users(name);

-- Roles Table
CREATE TABLE Roles (
                       id SERIAL PRIMARY KEY,
                       name TEXT UNIQUE NOT NULL,
                       description TEXT,
                       created DATE NOT NULL DEFAULT CURRENT_DATE,
                       updated DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_roles_name ON Roles(name);

-- Permissions Table
CREATE TABLE Permissions (
                             id SERIAL PRIMARY KEY,
                             name TEXT UNIQUE NOT NULL,
                             description TEXT,
                             created DATE NOT NULL DEFAULT CURRENT_DATE,
                             updated DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_permissions_name ON Permissions(name);

-- Operations Table
CREATE TABLE Operations (
                            id SERIAL PRIMARY KEY,
                            name TEXT UNIQUE NOT NULL,
                            description TEXT,
                            created DATE NOT NULL DEFAULT CURRENT_DATE,
                            updated DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_operations_name ON Operations(name);

-- UserRolesJunction Table
CREATE TABLE UserRolesJunction (
                                   userID INT REFERENCES Users(id) ON DELETE CASCADE,
                                   roleID INT REFERENCES Roles(id) ON DELETE CASCADE,
                                   PRIMARY KEY (userID, roleID)
);

CREATE INDEX idx_user_roles_userid ON UserRolesJunction(userID);
CREATE INDEX idx_user_roles_roleid ON UserRolesJunction(roleID);

-- RolesPermissionJunction Table
CREATE TABLE RolesPermissionJunction (
                                         roleID INT REFERENCES Roles(id) ON DELETE CASCADE,
                                         permissionID INT REFERENCES Permissions(id) ON DELETE CASCADE,
                                         PRIMARY KEY (roleID, permissionID)
);

CREATE INDEX idx_roles_permission_roleid ON RolesPermissionJunction(roleID);
CREATE INDEX idx_roles_permission_permissionid ON RolesPermissionJunction(permissionID);

-- OperationRoleJunction Table
CREATE TABLE OperationRoleJunction (
                                       operationID INT REFERENCES Operations(id) ON DELETE CASCADE,
                                       roleID INT REFERENCES Roles(id) ON DELETE CASCADE,
                                       PRIMARY KEY (operationID, roleID)
);

CREATE INDEX idx_operation_role_operationid ON OperationRoleJunction(operationID);
CREATE INDEX idx_operation_role_roleid ON OperationRoleJunction(roleID);
