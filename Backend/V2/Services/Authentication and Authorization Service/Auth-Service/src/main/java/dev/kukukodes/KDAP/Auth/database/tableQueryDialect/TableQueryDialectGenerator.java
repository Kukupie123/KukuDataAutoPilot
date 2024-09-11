package dev.kukukodes.KDAP.Auth.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TableQueryDialectGenerator {
    private final TableQueryDialectAdapter dialectAdapter;

    public TableQueryDialectGenerator(TableQueryDialectAdapter dialectAdapter) {
        this.dialectAdapter = dialectAdapter;
    }

    public String createUserTable(List<ColumnDefinition> columns) {
        StringBuilder query = new StringBuilder().append(dialectAdapter.createTableQuery(DbConstants.TableNames.Users));
        for (int i = 0; i < columns.size(); i++) {
            query.append(dialectAdapter.parseColumnForCreateTable(columns.get(i)));
            if (i != columns.size() - 1) {
                query.append(dialectAdapter.doAfterEachFieldForCreateTable());
            }
        }
        query.append(dialectAdapter.createTableAfterFields());
        String queryString = query.toString();
        log.info("Generated query : \n{}", queryString);
        return queryString;
    }

    public String dropUserTable() {
        return dialectAdapter.dropTableQuery(DbConstants.TableNames.Users);
    }
}
