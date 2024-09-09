CREATE TABLE IF NOT EXISTS Users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     userID VARCHAR(255) UNIQUE,
    passwordHash VARCHAR(255),
    userDesc TEXT,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    lastActivity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS Roles (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) UNIQUE,
    desc TEXT,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );
