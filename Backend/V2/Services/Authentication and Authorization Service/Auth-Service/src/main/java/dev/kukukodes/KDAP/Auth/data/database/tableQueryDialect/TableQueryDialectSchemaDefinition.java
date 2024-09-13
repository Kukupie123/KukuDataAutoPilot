package dev.kukukodes.KDAP.Auth.data.database.tableQueryDialect;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.enums.database.tableQueryDialect.TableColumnDataType;
import dev.kukukodes.KDAP.Auth.models.database.tableQueryDialect.ColumnDefinition;

import java.util.List;

/**
 * Static class holding columns of tables
 */
public class TableQueryDialectSchemaDefinition {
    public static List<ColumnDefinition> UserTableColumns = List.of(
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.id, TableColumnDataType.AUTOINCREMENT_INT, true, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.name, TableColumnDataType.TEXT, false, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.UsersTable.passwordHash, TableColumnDataType.TEXT, false, false, false),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.description, TableColumnDataType.TEXT, false, true, false),
            getCreatedColumn(),
            getUpdatedColumn(),
            new ColumnDefinition(DbConstants.TableColumnNames.UsersTable.lastActivity, TableColumnDataType.DATE, false, false, false),
            new ColumnDefinition(DbConstants.TableColumnNames.UsersTable.status, TableColumnDataType.TEXT, false, false, false)
    );

    public static List<ColumnDefinition> RoleTableColumns = List.of(
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.id, TableColumnDataType.AUTOINCREMENT_INT, true, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.name, TableColumnDataType.TEXT, false, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.description, TableColumnDataType.TEXT, false, true, false),
            getCreatedColumn(),
            getUpdatedColumn()
    );

    public static List<ColumnDefinition> PermissionTableColumns = List.of(
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.id, TableColumnDataType.AUTOINCREMENT_INT, true, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.name, TableColumnDataType.TEXT, false, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.description, TableColumnDataType.TEXT, false, true, false),
            getCreatedColumn(),
            getUpdatedColumn()
    );

    public static List<ColumnDefinition> OperationTableColumns = List.of(
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.id, TableColumnDataType.AUTOINCREMENT_INT, true, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.name, TableColumnDataType.TEXT, false, false, true),
            new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.description, TableColumnDataType.TEXT, false, true, false),
            getCreatedColumn(),
            getUpdatedColumn()
    );

    //For consistency
    private static ColumnDefinition getCreatedColumn() {
        return new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.created, TableColumnDataType.DATE, false, false, false);
    }

    //For consistency
    private static ColumnDefinition getUpdatedColumn() {
        return new ColumnDefinition(DbConstants.TableColumnNames.CommonColumns.updated, TableColumnDataType.DATE, false, false, false);
    }

}
