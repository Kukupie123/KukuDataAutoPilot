package dev.kukukodes.kdap.authenticationservice.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.kukukodes.kdap.authenticationservice.constants.DbConst;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDate;
@Table(DbConst.TableNames.USER_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements Serializable {
    @Id
    @Column(DbConst.ColumnNames.ID)
    String id;
    @Column(DbConst.ColumnNames.NAME)
    String name;
    @Column(DbConst.ColumnNames.PASSWORD)
    String password;
    @Column(DbConst.ColumnNames.CREATED)
    LocalDate created;
    @Column(DbConst.ColumnNames.UPDATED)
    LocalDate updated;
    @Column(DbConst.ColumnNames.EMAIL)
    String email;
    @Column(DbConst.ColumnNames.PICTURE)
    String picture;

    @Transient
    public static UserEntity createUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle) {
        return new UserEntity(
                oAuth2UserInfoGoogle.getSub(),
                oAuth2UserInfoGoogle.getName(),
                "",
                LocalDate.now(),
                LocalDate.now(),
                oAuth2UserInfoGoogle.getEmailID(),
                oAuth2UserInfoGoogle.getPictureURL()
        );
    }

}
