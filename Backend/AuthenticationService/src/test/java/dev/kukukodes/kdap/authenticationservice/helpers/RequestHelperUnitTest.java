package dev.kukukodes.kdap.authenticationservice.helpers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) //To use inject mocks
class RequestHelperUnitTest {

    @InjectMocks
    RequestHelper requestHelper;

    @Test
    void test() {
        var token = requestHelper.extractToken("Bearer dummyToken");
        Assertions.assertThat(token).isNotNull().isEqualTo("dummyToken");
    }
}
