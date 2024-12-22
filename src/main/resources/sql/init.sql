CREATE TABLE chats
(
    id         VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP
);

CREATE TABLE chat_messages
(
    id         VARCHAR(255) PRIMARY KEY,
    chat_id    VARCHAR(255),
    prompt     TEXT,
    response   TEXT,
    created_at TIMESTAMP
);