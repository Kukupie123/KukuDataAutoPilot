package dev.kukukodes.kdap.authenticationservice.dto.user;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


class UserRequestDTOUnitTest {

    @Test
    void fromUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("testMail");
        userEntity.setName("testName");
        userEntity.setPassword("testPassword");
        userEntity.setCreated(LocalDate.now());
        userEntity.setUpdated(LocalDate.now());
        userEntity.setPicture("testPicture");

        UserRequestDTO userRequestDTO = UserRequestDTO.fromUserEntity(userEntity);
        Assertions.assertThat(userRequestDTO)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", userEntity.getEmail())
                .hasFieldOrPropertyWithValue("name", userEntity.getName())
                .hasFieldOrPropertyWithValue("created", userEntity.getCreated())
                .hasFieldOrPropertyWithValue("updated", userEntity.getUpdated())
                .hasFieldOrPropertyWithValue("picture", userEntity.getPicture())
        ;
    }

}
