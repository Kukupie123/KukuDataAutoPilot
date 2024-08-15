import { createLogger, format, transports, Logger as WinstonLogger } from "winston";
//TODO: Make it better by adding a function that can also handle custom levels.
//If we use winton we can't use custom level easily such as log.info is possible but log.request isn't possible which is our custom level
export class Logger {
    private static loggerInstances: Map<string, number> = new Map();

    public static CreateLogger(className: string): WinstonLogger {
        // Get current instance count for the class
        let instanceCount = this.loggerInstances.get(className) || 0;

        // Update the instance count
        this.loggerInstances.set(className, instanceCount + 1);

        // Generate a unique identifier
        const objectIdentifier = `${className}-${instanceCount}`;

        // Create and return the Winston logger
        return createLogger({
            levels: {
                error: 0,
                warn: 1,
                info: 2,
                debug: 3,
                request: 4, // Custom level for requests and responses
            },
            format: format.combine(
                format.timestamp(),
                format.printf(({ timestamp, level, message }) => {
                    return `${timestamp} [${level}] [${objectIdentifier}]: ${message}`;
                })
            ),
            transports: [
                new transports.Console({
                    level: 'debug',
                }),
                new transports.File({
                    filename: 'logs/error.log',
                    level: 'error',
                }),
                new transports.File({
                    filename: 'logs/combined.log',
                    level: 'info',
                }),
                new transports.File({
                    filename: 'logs/request.log',
                    level: 'request', // Custom file for request logs
                }),
                new transports.Console({
                    level: 'request'
                })
            ],
        });
    }
}
