package dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.extra;

import dev.kukukodes.KDAP.Auth.Service.db.enums.TableColumnDataType;
import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.models.ColumnDefinition;

import java.util.List;

public class TableSchemaDefinition {
    public static List<ColumnDefinition> getUserTableColumns() {
        return List.of(
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
}
