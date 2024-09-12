package dev.kukukodes.KDAP.Auth.constants.database;

public class DbConstants {
    public static class TableNames {
        public static final String Users = "Users";
        public static final String Roles = "Roles";
        public static final String Permissions = "Permissions";
        public static final String Operations = "Operations";
        public static final String UserRolesJunction = "UserRolesJunction";
        public static final String RolesPermissionJunction = "RolesPermissionJunction";
        public static final String OperationRoleJunction = "OperationRoleJunction";

    }

    public static class TableColumnNames {
        public static class UsersTable {
            public static final String id = "id";
            public static final String passwordHash = "passwordHash";
            public static final String lastActivity = "lastActivity";
            public static final String status = "status";

        }

        public static  class CommonColumns{
            public static final String id = "id";
            public static final String name = "name";
            public static final String created = "created";
            public static final String updated = "updated";
            public static final String description = "description";
        }
    }

    public static class TableQueryDialect {
        public static final String Postgresql = "Postgresql";
        public static final String MySql = "MySql";

    }
}
