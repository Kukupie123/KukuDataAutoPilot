import fs from 'fs';
import path from 'path';
import { Category, LOG_DIRECTORY } from "../config/kdapLogger.config";

/**
 * A logger class for KDAP which supports Levels, Categories, File Logs
 */
export interface LogData {
    msg: string,
    category?: Category;
    func?: Function;
}
//TODO: Use object identifier to determine the instance. This will allow single class to have multiple loggers without increasing the instnce Count
export class KDAPLogger {
    private identifier: string;
    private instanceNumber: number;

    // Static maps to track instance counts and instance numbers
    private static instanceCounts: Map<string, number> = new Map();
    private static instanceNumbers: Map<string, number> = new Map();
    constructor(identifier: string) {
        this.identifier = identifier;

        // Initialize or increment instance count
        const currentCount = KDAPLogger.instanceCounts.get(identifier) || 0;
        KDAPLogger.instanceCounts.set(identifier, currentCount + 1);

        // Initialize the instance number for the current instance
        const instanceNumber = (KDAPLogger.instanceNumbers.get(identifier) || 0) + 1;
        KDAPLogger.instanceNumbers.set(identifier, instanceNumber);
        this.instanceNumber = instanceNumber;

        // Ensure the log directory exists
        if (!fs.existsSync(LOG_DIRECTORY)) {
            fs.mkdirSync(LOG_DIRECTORY, { recursive: true });
            console.log(`Created log directory at ${LOG_DIRECTORY}`);
        }

        // Create log files for all categories
        Object.values(Category).forEach(category => {
            const logFilePath = path.join(LOG_DIRECTORY, `${category}.log`);
            // Create an empty file if it doesn't exist
            if (!fs.existsSync(logFilePath)) {
                fs.writeFileSync(logFilePath, '');
                console.log(`Created log file at ${logFilePath}`);
            }
        });
    }

    private formatMessage(logData: LogData): string {
        const { msg, category = Category.Info, func } = logData;
        const timestamp = new Date().toISOString();

        // Build the log message parts
        const parts = [
            `[${this.identifier}-${this.instanceNumber}]`,
            func ? `[${func.name}]` : null, // Only add this part if func is defined
            `[${category}]`,
            `[${timestamp}]`,
            `: ${msg}`
        ];

        // Filter out any null parts and join them with a space
        return parts.filter(part => part !== null).join(' ');
    }





    /**
     * 
     * @param msg The message to log
     * @param category Optional. To change the log category. By default will be the one set in constructor
     */
    public log(logData: LogData) {
        const { msg, category = Category.Info, func } = logData;

        // Format the message
        const formattedMessage = this.formatMessage(logData);

        // Log to the console
        console.log(formattedMessage);

        // Define the log file path
        const logFilePath = path.join(LOG_DIRECTORY, `${category}.log`);

        // Append the message to the corresponding log file
        try {
            fs.appendFileSync(logFilePath, formattedMessage + '\n');
        } catch (error) {
            console.error(`Failed to write to log file ${logFilePath}:`, error);
        }
    }


    // Method to get the instance count for a given identifier
    public static getInstanceCount(identifier: string): number {
        return KDAPLogger.instanceCounts.get(identifier) || 0;
    }

    // Method to get the instance number for a specific identifier
    public static getInstanceNumber(identifier: string, instanceNumber: number): number {
        return KDAPLogger.instanceNumbers.get(identifier) || instanceNumber;
    }
}
