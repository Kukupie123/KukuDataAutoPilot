package dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.models;

import dev.kukukodes.KDAP.Auth.Service.db.enums.TableColumnDataType;
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
