package dev.kukukodes.KDAP.Auth.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.data.database.tableQueryDialect.TableQueryDialectSchemaDefinition;
import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TableQueryDialectGenerator {

    public final PermissionTableQueryDialectGenerator permissionGenerator;
    public final UsersTableQueryDialectGenerator userGenerator;
    public final RolesTableQueryDialectGenerator roleGenerator;
    public final OperationTableQueryDialectGenerator operationGenerator;
    @Autowired
    private TableQueryDialectAdapter dialectAdapter;


    public TableQueryDialectGenerator() {
        this.userGenerator = new UsersTableQueryDialectGenerator();
        this.roleGenerator = new RolesTableQueryDialectGenerator();
        this.permissionGenerator = new PermissionTableQueryDialectGenerator();
        this.operationGenerator = new OperationTableQueryDialectGenerator();
    }

    public String createTable(String tableName, List<ColumnDefinition> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialectAdapter.getCreateTableDialect().startCreateTable(tableName, columns));
        sql.append(dialectAdapter.getCreateTableDialect().beforePopulatingColumns(tableName, columns));
        sql.append(dialectAdapter.getCreateTableDialect().populateColumns(tableName, columns));
        sql.append(dialectAdapter.getCreateTableDialect().afterPopulatingColumns(tableName, columns));
        sql.append(dialectAdapter.getCreateTableDialect().endCreateTable(tableName, columns));
        log.info("Generated CreateTable dialect : {}", sql);
        return sql.toString();
    }

    public String dropTable(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialectAdapter.getDropTableDialect().beforeDropTable(tableName));
        sql.append(dialectAdapter.getDropTableDialect().dropTable(tableName));
        sql.append(dialectAdapter.getDropTableDialect().afterDropTable(tableName));
        log.info("Generated DropTable dialect : {}", sql);
        return sql.toString();
    }

    private interface tableGenerator {
        String createTable();

        String dropTable();
    }

    public class UsersTableQueryDialectGenerator implements tableGenerator {

        @Override
        public String createTable() {
            return TableQueryDialectGenerator.this.createTable(DbConstants.TableNames.Users, TableQueryDialectSchemaDefinition.UserTableColumns);
        }

        @Override
        public String dropTable() {
            return TableQueryDialectGenerator.this.dropTable(DbConstants.TableNames.Users);
        }
    }

    public class RolesTableQueryDialectGenerator implements tableGenerator {

        @Override
        public String createTable() {
            List<ColumnDefinition> columns = TableQueryDialectSchemaDefinition.RoleTableColumns;

            return TableQueryDialectGenerator.this.createTable(DbConstants.TableNames.Roles, columns);
        }

        @Override
        public String dropTable() {
            return TableQueryDialectGenerator.this.dropTable(DbConstants.TableNames.Roles);
        }
    }

    public class PermissionTableQueryDialectGenerator implements tableGenerator {

        @Override
        public String createTable() {
            List<ColumnDefinition> columns = TableQueryDialectSchemaDefinition.PermissionTableColumns;
            return TableQueryDialectGenerator.this.createTable(DbConstants.TableNames.Permissions, columns);
        }

        @Override
        public String dropTable() {
            return TableQueryDialectGenerator.this.dropTable(DbConstants.TableNames.Permissions);
        }
    }

    public class OperationTableQueryDialectGenerator implements tableGenerator {

        @Override
        public String createTable() {
            return TableQueryDialectGenerator.this.createTable(DbConstants.TableNames.Operations, TableQueryDialectSchemaDefinition.OperationTableColumns);
        }

        @Override
        public String dropTable() {
            return TableQueryDialectGenerator.this.dropTable(DbConstants.TableNames.Operations);
        }
    }
}
