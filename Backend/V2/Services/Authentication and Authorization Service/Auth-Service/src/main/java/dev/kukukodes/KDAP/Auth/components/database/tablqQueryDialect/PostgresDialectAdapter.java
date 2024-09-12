package dev.kukukodes.KDAP.Auth.components.database.tablqQueryDialect;

import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.TableQueryDialectAdapter;
import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;
import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(DbConstants.TableQueryDialect.Postgresql)
public class PostgresDialectAdapter implements TableQueryDialectAdapter {

    public String parseColumnForCreateTable(ColumnDefinition column) {
        StringBuilder query = new StringBuilder();
        query.append(column.getColumnName().trim()); //<name>
        String type;
        switch (column.getColumnType()) {
            case INT -> type = "INT";
            case DATE -> type = "DATE";
            case TEXT -> type = "TEXT";
            case AUTOINCREMENT_INT -> type = "SERIAL";
            case BOOL -> type = "BOOL";
            case FLOAT -> type = "DECIMAL(10,5)"; //10 digits and 5 after point.
            default -> throw new IllegalArgumentException("Invalid column type: " + column.getColumnType());
        }
        query.append(" ").append(type); //<name> <type>
        if (column.isPrimaryKey()) {
            query.append(" PRIMARY KEY"); //If PK primary key, unique and not null is not mandatory
            return query.toString();
        }
        if (column.isUnique()) {
            query.append(" UNIQUE");
        }
        if (!column.isCanBeNull()) {
            query.append(" NOT NULL");
        }
        return query.toString();
    }

    @Override
    public CreateTableDialect getCreateTableDialect() {
        return new CreateTableDialect() {
            @Override
            public String startCreateTable(String tableName, List<ColumnDefinition> columns) {
                return "CREATE TABLE IF NOT EXISTS " + tableName;
            }

            @Override
            public String beforePopulatingColumns(String tableName, List<ColumnDefinition> columns) {
                return " ( ";
            }

            @Override
            public String populateColumns(String tableName, List<ColumnDefinition> columns) {
                StringBuilder sql = new StringBuilder();
                for (var i = 0; i < columns.size(); i++) {

                    String columnSyntax = parseColumnForCreateTable(columns.get(i));
                    sql.append(columnSyntax);
                    //Add a comma if it isn't the last element
                    if (i != columns.size() - 1) {
                        sql.append(", ");
                    }
                }

                //should look like "id SERIAL PRIMARY KEY, name TEXT NOT NULL UNIQUE"
                return sql.toString();
            }

            @Override
            public String afterPopulatingColumns(String tableName, List<ColumnDefinition> columns) {
                return ")";
            }

            @Override
            public String endCreateTable(String tableName, List<ColumnDefinition> columns) {
                return ";";
            }
        };
    }

    @Override
    public DropTableDialect getDropTableDialect() {
        return new DropTableDialect() {
            @Override
            public String beforeDropTable(String tableName) {
                return "DROP TABLE IF EXISTS ";
            }

            @Override
            public String dropTable(String tableName) {
                return tableName;
            }

            @Override
            public String afterDropTable(String tableName) {
                return ";";
            }
        };
    }
}
