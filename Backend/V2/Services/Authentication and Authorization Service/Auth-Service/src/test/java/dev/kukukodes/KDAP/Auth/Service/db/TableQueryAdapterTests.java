package dev.kukukodes.KDAP.Auth.Service.db;

import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.extra.TableSchemaDefinition;
import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.TableQueryGenerator;
import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.implementation.postgres.PostgresDialectAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TableQueryAdapterTests {
    @Test
    void testTableQueryGenerator() {
        String query = TableQueryGenerator.createUserTable(new PostgresDialectAdapter(), TableSchemaDefinition.getUserTableColumns());
        log.info(query);
    }
}
