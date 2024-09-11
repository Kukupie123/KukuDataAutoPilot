package dev.kukukodes.KDAP.Auth.db;

import dev.kukukodes.KDAP.Auth.data.database.tableQueryDialect.TableSchemaDefinition;
import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.TableQueryDialectGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TableQueryAdapterTests {
    @Autowired
    TableQueryDialectGenerator tableQueryDialectGenerator;

    @Test
    void testTableQueryGenerator() {
        String query = tableQueryDialectGenerator.createUserTable(TableSchemaDefinition.UserTableColumns);
        log.info(query);
    }
}
