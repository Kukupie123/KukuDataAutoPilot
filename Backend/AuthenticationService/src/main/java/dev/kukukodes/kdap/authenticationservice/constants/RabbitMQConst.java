package dev.kukukodes.kdap.authenticationservice.constants;

public class RabbitMQConst {
    private static final String appName = System.getProperty(EnvNamesConst.NAME);

    public static class Queues {
        public static final String USER_UPDATED = appName + "." + Exchanges.USER_EVENT + ".updated";
    }

    public static class Routes {
        public static final String USER_UPDATED = Exchanges.USER_EVENT + ".updated";

    }

    public class Exchanges {
        public static final String USER_EVENT = "user";
    }
}
