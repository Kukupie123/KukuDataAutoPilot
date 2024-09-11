package dev.kukukodes.KDAP.Auth.components.database;

import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.DatabaseDialectAdapter;
import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;
import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import org.springframework.stereotype.Component;

@Component(DbConstants.TableQueryDialect.Postgresql)
public class PostgresDialectAdapter implements DatabaseDialectAdapter {


    @Override
    public String createTableQuery(String tableName) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " ( \n";
    }

    @Override
    public String createTableAfterFields() {
        return " );";
    }

    @Override
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
    public String doAfterEachFieldForCreateTable() {
        return ", \n";
    }

    @Override
    public String dropTableQuery(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName + ";";
    }
}
