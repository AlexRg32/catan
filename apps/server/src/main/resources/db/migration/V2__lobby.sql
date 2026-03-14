CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    code VARCHAR(16) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    host_user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL,
    max_players INTEGER NOT NULL DEFAULT 4,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE room_seats (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    seat_index INTEGER NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    ready BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_room_seat UNIQUE (room_id, seat_index),
    CONSTRAINT uq_room_user UNIQUE (room_id, user_id)
);

CREATE INDEX idx_room_seats_room_id ON room_seats (room_id);
