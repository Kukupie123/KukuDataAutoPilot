CREATE TABLE  IF NOT EXISTS users(
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    password VARCHAR(255),
    created DATE,
    updated DATE,
    activity DATE
);
