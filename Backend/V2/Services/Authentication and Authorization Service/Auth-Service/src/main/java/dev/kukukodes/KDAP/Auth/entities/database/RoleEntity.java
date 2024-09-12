package dev.kukukodes.KDAP.Auth.entities.database;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table(DbConstants.TableNames.Roles)
@Data
public class RoleEntity {
    @Id
    @Column(DbConstants.TableColumnNames.CommonColumns.id)
    private Integer id;
    @Column(DbConstants.TableColumnNames.CommonColumns.name)
    private String name;
    @Column(DbConstants.TableColumnNames.CommonColumns.description)
    private String desc;
    @Column(DbConstants.TableColumnNames.CommonColumns.created)
    private Date created;
    @Column(DbConstants.TableColumnNames.CommonColumns.updated)
    private Date updated;

}
