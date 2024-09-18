package dev.kukukodes.kdap.authenticationservice.entity;

import dev.kukukodes.kdap.authenticationservice.constants.DbConstants;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

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
    LocalDate created;
    @Column(DbConstants.ColumnNames.UPDATED)
    LocalDate updated;
    @Column(DbConstants.ColumnNames.ACTIVITY)
    LocalDate activity;
    @Column(DbConstants.ColumnNames.EMAIL)
    String email;
    @Column(DbConstants.ColumnNames.PICTURE)
    String picture;

    @Transient
    public static UserEntity createUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle) {
        return new UserEntity(
                oAuth2UserInfoGoogle.getSub(),
                oAuth2UserInfoGoogle.getName(),
                "",
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now(),
                oAuth2UserInfoGoogle.getEmailID(),
                oAuth2UserInfoGoogle.getPictureURL()
        );
    }

    @Transient
    public static UserEntity updateUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle, UserEntity userEntity) {
        userEntity.setActivity(LocalDate.now());
        userEntity.setName(oAuth2UserInfoGoogle.getName());
        userEntity.setPicture(oAuth2UserInfoGoogle.getPictureURL());
        userEntity.setEmail(oAuth2UserInfoGoogle.getEmailID());
        return userEntity;
    }
}
