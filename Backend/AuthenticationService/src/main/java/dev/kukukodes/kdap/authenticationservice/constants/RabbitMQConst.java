package dev.kukukodes.kdap.authenticationservice.constants;

import org.springframework.beans.factory.annotation.Value;

public class RabbitMQConst {

    @Value("${spring.application.name}")
    public static String appName;

    public static class Exchanges {
        public static final String USER_EVENT = "user";
    }

    public static class Queues {
        //Queue name is unique because we do not want any other consumer to have the same queue name and consume the message.
        public static final String UPDATED = appName + "." + "user.updated";
    }

    public static class Routes{
        public static final String USER_UPDATED = "user.updated";
    }
}
