package dev.kukukodes.kdap.authenticationservice.models.userModels;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * KDAP User info holds google provider details
 */
@AllArgsConstructor
@Getter
@ToString
public class OAuth2UserInfoGoogle {
    private String emailID;
    private String name;
    private String pictureURL;
    private String sub; //Use sub as ID as it's constant.
    // Note that sub will be consistent but different for every project in GCP.

    public static OAuth2UserInfoGoogle fromOAuth2User(OAuth2User googleUser) {
        var attributes = googleUser.getAttributes();
        return new OAuth2UserInfoGoogle(attributes.get("email").toString(), attributes.get("name").toString(), attributes.get("picture").toString(), attributes.get("sub").toString());
    }

}
