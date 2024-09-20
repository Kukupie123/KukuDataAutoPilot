package dev.kukukodes.kdap.authenticationservice.constants;

import org.springframework.beans.factory.annotation.Value;

public class RabbitConst {
    @Value("${spring.application.name}")
    static String appName;
    public static class Exchanges {

        public static final class UserEvent {
            public static final String EXCHANGE = "user_event";

            public static final class queueTypes {

                public static final String userUpdated = EXCHANGE + "." + "user_updated_internal";

            }
        }

    }
}
