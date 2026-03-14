CREATE TABLE game_audit (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    command_id VARCHAR(120),
    actor_player_id UUID,
    command_type VARCHAR(120) NOT NULL,
    outcome VARCHAR(30) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_game_audit_game_created_at ON game_audit (game_id, created_at);
