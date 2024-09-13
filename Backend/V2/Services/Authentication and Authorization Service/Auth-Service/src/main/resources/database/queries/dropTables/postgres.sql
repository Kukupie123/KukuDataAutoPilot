-- Drop junction tables first to avoid foreign key constraint issues
DROP TABLE IF EXISTS OperationRoleJunction;
DROP TABLE IF EXISTS RolesPermissionJunction;
DROP TABLE IF EXISTS UserRolesJunction;

-- Drop main tables
DROP TABLE IF EXISTS Operations;
DROP TABLE IF EXISTS Permissions;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Users;

-- Drop any leftover indexes (though they should be dropped with their tables)
DROP INDEX IF EXISTS idx_operation_role_roleid;
DROP INDEX IF EXISTS idx_operation_role_operationid;
DROP INDEX IF EXISTS idx_roles_permission_permissionid;
DROP INDEX IF EXISTS idx_roles_permission_roleid;
DROP INDEX IF EXISTS idx_user_roles_roleid;
DROP INDEX IF EXISTS idx_user_roles_userid;
DROP INDEX IF EXISTS idx_operations_name;
DROP INDEX IF EXISTS idx_permissions_name;
DROP INDEX IF EXISTS idx_roles_name;
DROP INDEX IF EXISTS idx_users_name;
