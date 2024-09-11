package dev.kukukodes.KDAP.Auth.db.Entities;

import dev.kukukodes.KDAP.Auth.db.contants.DbConstants;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

/**
 * Represents a user in the database.
 * Maps to the 'Users' table in the database.
 */
@Table(DbConstants.TableNames.Users)
@Data
public class UserEntity {
    @Id
    private Integer id;

    @Column(DbConstants.TableColumnNames.UsersTable.userID)
    private String userID;

    @Column(DbConstants.TableColumnNames.UsersTable.passwordHash)
    private String passwordHash;

    @Column(DbConstants.TableColumnNames.UsersTable.userDesc)
    private String userDesc;

    @Column(DbConstants.TableColumnNames.UsersTable.created)
    private LocalDate created;

    @Column(DbConstants.TableColumnNames.UsersTable.updated)
    private LocalDate updated;

    @Column(DbConstants.TableColumnNames.UsersTable.lastActivity)
    private LocalDate lastActivity;

    @Column(DbConstants.TableColumnNames.UsersTable.status)
    private String status;


}
