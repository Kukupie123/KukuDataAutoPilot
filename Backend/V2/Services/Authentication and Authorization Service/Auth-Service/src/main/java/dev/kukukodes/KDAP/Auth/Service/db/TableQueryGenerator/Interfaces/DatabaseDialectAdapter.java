package dev.kukukodes.KDAP.Auth.Service.db.TableQueryGenerator.Interfaces;

import dev.kukukodes.KDAP.Auth.Service.db.TableQueryGenerator.models.ColumnDefinition;

public interface DatabaseDialectAdapter {
    ///Eg :- "CREATE TABLE IF NOT EXIST"
    String createTableQuery(String tableName);
    ///Eg :- ");"
    String createTableAfterFields();
    ///Eg <name> <type> <PK> <etc> <etc>
    String parseColumnForCreateTable(ColumnDefinition column);
    ///Eg :- Go to next line after putting column data
    String doAfterEachFieldForCreateTable();
}
