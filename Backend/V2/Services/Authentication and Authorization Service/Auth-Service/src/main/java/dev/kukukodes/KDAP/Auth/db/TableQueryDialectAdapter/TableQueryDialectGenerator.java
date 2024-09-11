package dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter;

import dev.kukukodes.KDAP.Auth.db.contants.DbConstants;
import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.adapters.DatabaseDialectAdapter;
import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.models.ColumnDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TableQueryDialectGenerator {
    public static String createUserTable(DatabaseDialectAdapter tableQueryAdapter, List<ColumnDefinition> columns) {
        StringBuilder query = new StringBuilder().append(tableQueryAdapter.createTableQuery(DbConstants.TableNames.Users));
        for (int i = 0; i < columns.size(); i++) {
            query.append(tableQueryAdapter.parseColumnForCreateTable(columns.get(i)));
            if (i != columns.size() - 1) {
                query.append(tableQueryAdapter.doAfterEachFieldForCreateTable());
            }
        }
        query.append(tableQueryAdapter.createTableAfterFields());
        String queryString = query.toString();
        log.info("Generated query : \n{}", queryString);
        return queryString;
    }

    public static String dropUserTable(DatabaseDialectAdapter tableQueryAdapter) {
        return tableQueryAdapter.dropTableQuery(DbConstants.TableNames.Users);
    }
}
