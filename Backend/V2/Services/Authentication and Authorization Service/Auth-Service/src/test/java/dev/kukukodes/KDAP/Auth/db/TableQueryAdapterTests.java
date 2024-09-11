package dev.kukukodes.KDAP.Auth.db;

import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.data.TableSchemaDefinition;
import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.TableQueryDialectGenerator;
import dev.kukukodes.KDAP.Auth.db.components.tableQueryDialect.PostgresDialectAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TableQueryAdapterTests {
    @Test
    void testTableQueryGenerator() {
        String query = TableQueryDialectGenerator.createUserTable(new PostgresDialectAdapter(), TableSchemaDefinition.UserTableColumns);
        log.info(query);
    }
}
