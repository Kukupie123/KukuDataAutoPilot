import fs from 'fs';
import path from 'path';
import { Category, LOG_DIRECTORY } from "../config/kdapLogger.config";

/**
 * A logger class for KDAP which supports Levels, Categories, File Logs
 */
//TODO: Use object identifier to determine the instance. This will allow single class to have multiple loggers without increasing the instnce Count
export class KDAPLogger {
    private identifier: string;
    private instanceNumber: number;

    // Static maps to track instance counts and instance numbers
    private static instanceCounts: Map<string, number> = new Map();
    private static instanceNumbers: Map<string, number> = new Map();
    private category: Category;
    constructor(identifier: string, category: Category = Category.Info) {
        this.category = category;
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

    private formatMessage(category: Category, msg: string): string {
        const timestamp = new Date().toISOString();
        return `[${this.identifier}-${this.instanceNumber}] : [${category}] : [${msg}] : [${timestamp}]`;
    }


    public log(msg: string) {
        // Format the message
        const formattedMessage = this.formatMessage(this.category, msg);

        // Log to the console
        console.log(formattedMessage);

        // Define the log file path
        const logFilePath = path.join(LOG_DIRECTORY, `${this.category}.log`);

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
