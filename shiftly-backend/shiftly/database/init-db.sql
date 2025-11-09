-- ============================================
-- SQLite Datenbank: shiftly.db
-- Schema: user / events
-- ============================================

-- Fremdschlüssel aktivieren
PRAGMA foreign_keys = ON;

-- ========================
-- Tabelle: user
-- ========================
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    working_hours INTEGER
);

-- ========================
-- Tabelle: events
-- ========================
CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT(1000),
    start_timestamp DATETIME NOT NULL,
    end_timestamp DATETIME NOT NULL,
    is_break TINYINT(1) DEFAULT 0,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ========================
-- Indexe
-- ========================
CREATE INDEX IF NOT EXISTS idx_events_user_id ON events(user_id);
