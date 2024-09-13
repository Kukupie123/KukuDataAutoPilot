package dev.kukukodes.KDAP.Auth.entities.database;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * Represents a user in the database.
 * Maps to the 'Users' table in the database.
 */
@Table(DbConstants.TableNames.Users)
@AllArgsConstructor
@Getter
@ToString
public class UserEntity {

    @Id
    private Integer id;
    @Column(DbConstants.TableColumnNames.CommonColumns.name)
    private String name;
    @Column(DbConstants.TableColumnNames.UsersTable.passwordHash)
    private String passwordHash;
    @Column(DbConstants.TableColumnNames.CommonColumns.description)
    private String description;
    @Column(DbConstants.TableColumnNames.CommonColumns.created)
    private Date created;
    @Column(DbConstants.TableColumnNames.CommonColumns.updated)
    private Date updated;
    @Column(DbConstants.TableColumnNames.UsersTable.lastActivity)
    private Date lastActivity;
    @Column(DbConstants.TableColumnNames.UsersTable.status)
    private String status;

    public UserEntity(String name, String passwordHash, String description, Date created, Date updated, Date lastActivity, String status) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.lastActivity = lastActivity;
        this.id = null;
    }


}
