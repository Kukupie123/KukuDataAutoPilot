package dev.kukukodes.KDAP.Auth.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;

public interface TableQueryDialectAdapter {
    ///Eg :- "CREATE TABLE IF NOT EXIST"
    String createTableQuery(String tableName);

    ///Eg :- ");"
    String createTableAfterFields();

    ///Eg <name> <type> <PK> <etc> <etc>
    String parseColumnForCreateTable(ColumnDefinition column);

    ///Eg :- Go to next line after putting column data
    String doAfterEachFieldForCreateTable();

    ///Eg :- DROP TABLE IF EXISTS <name>
    String dropTableQuery(String tableName);
}
