package dev.kukukodes.KDAP.Auth.Service.entity;

import dev.kukukodes.KDAP.Auth.Service.enums.UserStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

/**
 * Represents a user in the database.
 * Maps to the 'Users' table in the database.
 */
@Table("Users") //Important: This helps R2DBC repository know which table to look for
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
    private UserStatus status;


}
