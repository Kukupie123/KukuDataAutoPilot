package dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.adapters;

import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.models.ColumnDefinition;

public interface DatabaseDialectAdapter {
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
