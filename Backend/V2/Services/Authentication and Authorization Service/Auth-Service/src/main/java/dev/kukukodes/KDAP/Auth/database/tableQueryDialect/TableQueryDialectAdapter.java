package dev.kukukodes.KDAP.Auth.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;

import java.util.List;

/**
 * Enables implementation of table operations.<br>
 * View {{@link dev.kukukodes.KDAP.Auth.components.database.tablqQueryDialect.PostgresDialectAdapter}} to see implementation.
 */
public interface TableQueryDialectAdapter {
    CreateTableDialect getCreateTableDialect();
    DropTableDialect getDropTableDialect();
    interface CreateTableDialect {
        /**
         * Postgresql example :- "CREATE TABLE IF NOT EXIST <tableName>"
         *
         * @param tableName name of the table to create
         * @param columns   columns that the table will have
         * @return 1st section query
         */
        String startCreateTable(String tableName, List<ColumnDefinition> columns);

        /**
         * Postgresql example :- " (\n"
         *
         * @param tableName name of the table to create
         * @param columns   columns that the table will have
         * @return 2nd section query
         */
        String beforePopulatingColumns(String tableName, List<ColumnDefinition> columns);

        /**
         * Postgresql example :- " id SERIAL PRIMARY KEY, name TEXT, NOT NULL,..."
         *
         * @param tableName name of the table to create
         * @param columns   columns that the table will have
         * @return 3rd section query
         */
        String populateColumns(String tableName, List<ColumnDefinition> columns);

        /**
         * Postgresql example :- ")"
         *
         * @param tableName name of the table to create
         * @param columns   columns that the table will have
         * @return 4th section query
         */
        String afterPopulatingColumns(String tableName, List<ColumnDefinition> columns);

        /**
         * Postgresql example :- ";"
         *
         * @param tableName name of the table to create
         * @param columns   columns that the table will have
         * @return 5th section query
         */
        String endCreateTable(String tableName, List<ColumnDefinition> columns);
    }

    interface DropTableDialect {
        String beforeDropTable(String tableName);

        String dropTable(String tableName);

        String afterDropTable(String tableName);

    }
}
