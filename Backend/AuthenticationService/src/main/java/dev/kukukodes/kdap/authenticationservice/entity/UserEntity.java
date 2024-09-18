package dev.kukukodes.kdap.authenticationservice.entity;

import dev.kukukodes.kdap.authenticationservice.constants.DbConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table(DbConstants.TableNames.USER_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(DbConstants.ColumnNames.ID)
    String id;
    @Column(DbConstants.ColumnNames.NAME)
    String name;
    @Column(DbConstants.ColumnNames.PASSWORD)
    String password;
    @Column(DbConstants.ColumnNames.CREATED)
    Date created;
    @Column(DbConstants.ColumnNames.UPDATED)
    Date updated;
    @Column(DbConstants.ColumnNames.ACTIVITY)
    Date activity;
    @Column(DbConstants.ColumnNames.EMAIL)
    String email;
    @Column(DbConstants.ColumnNames.PICTURE)
    String picture;
}
