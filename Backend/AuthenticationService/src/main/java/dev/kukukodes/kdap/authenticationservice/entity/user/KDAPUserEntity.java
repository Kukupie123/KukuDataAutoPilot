package dev.kukukodes.kdap.authenticationservice.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.constants.DbConst;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.userModels.OAuth2UserInfoGoogle;
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
@JsonInclude(JsonInclude.Include.NON_NULL)//Do not include null fields
public class KDAPUserEntity implements Serializable {
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
    String accessLevel;

    @Transient
    public static KDAPUserEntity createUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle, SecurityHelper securityHelper) {

        String accessLevel = AccessLevelConst.SELF;
        if (securityHelper.isSuperuser(oAuth2UserInfoGoogle.getSub())) {
            accessLevel = AccessLevelConst.ADMIN;
        }
        return new KDAPUserEntity(
                oAuth2UserInfoGoogle.getSub(),
                oAuth2UserInfoGoogle.getName(),
                "",
                LocalDate.now(),
                LocalDate.now(),
                oAuth2UserInfoGoogle.getEmailID(),
                oAuth2UserInfoGoogle.getPictureURL(),
                accessLevel
        );
    }

    /**
     * Immutable function that returns a new KDAPUserEntity with updated data from OAuth2UserInfoGoogle
     *
     * @return updated user
     */
    @Transient
    public KDAPUserEntity updatePropertiesFromOAuth2UserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle, SecurityHelper securityHelper) {
        String accessLevel = securityHelper.isSuperuser(oAuth2UserInfoGoogle.getSub()) ? AccessLevelConst.ADMIN : AccessLevelConst.SELF;
        var updatedUser = new KDAPUserEntity(this.getId(), this.getName(), this.password, this.created, this.updated, this.getEmail(), this.picture, accessLevel);
        updatedUser.setEmail(oAuth2UserInfoGoogle.getEmailID());
        updatedUser.setPicture(oAuth2UserInfoGoogle.getPictureURL());
        updatedUser.setName(oAuth2UserInfoGoogle.getName());
        updatedUser.setId(oAuth2UserInfoGoogle.getSub());
        return updatedUser;
    }

}
