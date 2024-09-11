package dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.enums.database.tableQueryDialect.TableColumnDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ColumnDefinition {
    private final String columnName;
    private final TableColumnDataType columnType;
    private final boolean isPrimaryKey;
    private final boolean canBeNull;
    private final boolean isUnique;
}
