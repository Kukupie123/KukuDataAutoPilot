package dev.kukukodes.kdap.authenticationservice.constants;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConst {
    private static String appName;
    /**
     * object variable with getters necessary for Spring Expression Language used at {@link dev.kukukodes.kdap.authenticationservice.consumers.UserEventListener}
     */
    @Getter
    private Exchanges exchanges;
    @Getter
    private Queues queues;
    @Getter
    private Routes routes;

    @Value("${spring.application.name}")
    public void setAppName(String appName) {
        RabbitMQConst.appName = appName;
    }

    @PostConstruct
    public void init() {
        exchanges = new Exchanges();
        queues = new Queues();
        routes = new Routes();
    }

    public class Exchanges {
        public static final String USER_EVENT = "user";

        public static String getUserExchange() {
            return USER_EVENT;
        }
    }

    public class Queues {
        public static final String USER_UPDATED = "updated";

        public static String getUserUpdatedQueue() {
            return appName + "." + Exchanges.USER_EVENT + "." + USER_UPDATED;
        }
    }

    public class Routes {
        public static final String USER_UPDATED = "updated";

        public static String getUserUpdatedRoute() {
            return Exchanges.USER_EVENT + "." + USER_UPDATED;
        }
    }
}
