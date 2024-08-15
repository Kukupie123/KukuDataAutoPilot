// loggerconfig.ts

// Define log categories as an enum
export enum Category {
    Debug = 'Debug',
    Info = 'Info',
    Warn = 'Warn',
    Error = 'Error',
    Fatal = 'Fatal',

    // Custom categories
    Database = 'Database',
    Factory = 'Factory',
    Controller = 'Controller',
    Service = 'Service'
}

// Define the log directory
export const LOG_DIRECTORY = process.env.LOG_DIRECTORY || './log'; // Use environment variable or default path
