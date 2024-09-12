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

    public UsersTableQueryDialectGenerator userGenerator;
    public RolesTableQueryDialectGenerator roleGenerator;
    @Autowired
    protected TableQueryDialectAdapter dialectAdapter;

    public TableQueryDialectGenerator() {
        this.userGenerator = new UsersTableQueryDialectGenerator();
        this.roleGenerator = new RolesTableQueryDialectGenerator();
    }

    public String createTable(String tableName, List<ColumnDefinition> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialectAdapter.createTable(tableName));
        sql.append(dialectAdapter.createTableBeforeFields());

        for (int i = 0; i < columns.size(); i++) {
            sql.append(dialectAdapter.createTableBeforeEachField(tableName));
            sql.append(dialectAdapter.parseColumnForCreateTable(columns.get(i)));
            if (i != columns.size() - 1) {
                sql.append(dialectAdapter.createTableAfterEachFieldLastField(tableName));
            }
        }
        sql.append(dialectAdapter.createTableAfterFields(tableName));
        log.info("Create table queryDialect generated : {}", sql);
        return sql.toString();
    }

    public String dropTable(String tableName) {
        StringBuilder sql = new StringBuilder();
        return sql.append(dialectAdapter.dropTableQuery(tableName)).toString();
    }

    public class UsersTableQueryDialectGenerator {
        public String createUserTable() {
            return createTable(DbConstants.TableNames.Users, TableQueryDialectSchemaDefinition.UserTableColumns);
        }

        public String dropUserTable() {
            return dropTable(DbConstants.TableNames.Users);
        }
    }

    public class RolesTableQueryDialectGenerator {
        public String createRoleTable() {
            List<ColumnDefinition> columns = TableQueryDialectSchemaDefinition.RoleTableColumns;
            return createTable(DbConstants.TableNames.Roles, columns);
        }

        public String dropRoleTable() {
            return dropTable(DbConstants.TableNames.Roles);
        }
    }
}
