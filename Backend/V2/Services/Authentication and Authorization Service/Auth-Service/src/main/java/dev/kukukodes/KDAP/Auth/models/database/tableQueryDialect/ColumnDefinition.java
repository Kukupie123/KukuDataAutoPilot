package dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.enums.database.tableQueryDialect.TableColumnDataType;

public record ColumnDefinition(String columnName, TableColumnDataType columnType, boolean isPrimaryKey,
                               boolean canBeNull, boolean isUnique) {
}
