package dev.kukukodes.KDAP.Auth.user;

import dev.kukukodes.KDAP.Auth.v2.repo.database.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("test")
public class UserTests {
    @Autowired
    private IUserRepository userRepo;
    @Test()
    void UsersListTest(){

        var users = userRepo.getAllUsers().collectList().block();
        assert users != null;
        users.forEach(user->{
            System.out.println(user.toString());
        });
    }
}
