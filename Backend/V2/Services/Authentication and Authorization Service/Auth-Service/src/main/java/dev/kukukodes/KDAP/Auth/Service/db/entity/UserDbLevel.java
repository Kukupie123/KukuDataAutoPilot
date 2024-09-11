package dev.kukukodes.KDAP.Auth.Service.db.entity;

import dev.kukukodes.KDAP.Auth.Service.db.contants.DbConstants;
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
public class UserDbLevel {

    @Id
    private Integer id;

    @Column("userID")
    private String userID;

    @Column("passwordHash")
    private String passwordHash;

    @Column("userDesc")
    private String userDesc;

    @Column("created")
    private LocalDate created;

    @Column("updated")
    private LocalDate updated;

    @Column("lastActivity")
    private LocalDate lastActivity;

    @Column("status")
    private String status;


}
