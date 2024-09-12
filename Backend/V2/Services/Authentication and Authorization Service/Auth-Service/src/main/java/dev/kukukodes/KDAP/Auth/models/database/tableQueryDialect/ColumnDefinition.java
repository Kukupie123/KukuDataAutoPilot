package dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.enums.database.tableQueryDialect.TableColumnDataType;
import lombok.Getter;

@Getter
public record ColumnDefinition(String columnName, TableColumnDataType columnType, boolean isPrimaryKey,
                               boolean canBeNull, boolean isUnique) {
}
