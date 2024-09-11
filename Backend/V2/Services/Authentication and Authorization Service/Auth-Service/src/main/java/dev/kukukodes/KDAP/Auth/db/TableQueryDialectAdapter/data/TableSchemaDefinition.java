package dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.data;

import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.enums.TableColumnDataType;
import dev.kukukodes.KDAP.Auth.db.TableQueryDialectAdapter.models.ColumnDefinition;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Static class holding columns of tables
 */
public class TableSchemaDefinition {
    public static List<ColumnDefinition> UserTableColumns = List.of(
            new ColumnDefinition("id", TableColumnDataType.AUTOINCREMENT_INT, true, false, true),
            new ColumnDefinition("userID", TableColumnDataType.TEXT, false, false, true),
            new ColumnDefinition("passwordHash", TableColumnDataType.TEXT, false, false, false),
            new ColumnDefinition("userDesc", TableColumnDataType.TEXT, false, true, false),
            new ColumnDefinition("created", TableColumnDataType.DATE, false, false, false),
            new ColumnDefinition("updated", TableColumnDataType.DATE, false, false, false),
            new ColumnDefinition("lastActivity", TableColumnDataType.DATE, false, false, false),
            new ColumnDefinition("status", TableColumnDataType.TEXT, false, false, false)
    );

}
