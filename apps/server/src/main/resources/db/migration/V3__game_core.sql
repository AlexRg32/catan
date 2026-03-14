CREATE TABLE games (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id),
    status VARCHAR(20) NOT NULL,
    seed BIGINT,
    current_turn_player_id UUID,
    phase VARCHAR(30),
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE game_players (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    color VARCHAR(20) NOT NULL,
    victory_points INTEGER NOT NULL DEFAULT 0,
    played_knights INTEGER NOT NULL DEFAULT 0,
    resources_summary VARCHAR(1024),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_game_player UNIQUE (game_id, user_id)
);

CREATE TABLE game_events (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    seq BIGINT NOT NULL,
    type VARCHAR(120) NOT NULL,
    actor_player_id UUID,
    payload TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_game_event_seq UNIQUE (game_id, seq)
);

CREATE TABLE game_snapshots (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    seq BIGINT NOT NULL,
    state_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_game_snapshot_seq UNIQUE (game_id, seq)
);
