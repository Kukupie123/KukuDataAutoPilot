package dev.kukukodes.KDAP.Auth.entities.database;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table(DbConstants.TableNames.Roles)
@AllArgsConstructor
@Getter
@ToString
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
    public RoleEntity(String name, String desc, Date created, Date updated) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.updated = updated;
        this.id = null;
    }

}
