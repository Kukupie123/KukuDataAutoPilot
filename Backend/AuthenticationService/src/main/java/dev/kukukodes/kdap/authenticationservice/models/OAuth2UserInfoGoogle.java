package dev.kukukodes.kdap.authenticationservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Represents User info of
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

    public static OAuth2UserInfoGoogle parse(OAuth2User googleUser) {
        var attributes = googleUser.getAttributes();
        return new OAuth2UserInfoGoogle(attributes.get("email").toString(), attributes.get("name").toString(), attributes.get("picture").toString(), attributes.get("sub").toString());
    }
}
