CREATE TABLE event_space_media (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    event_space_id UUID NOT NULL
        REFERENCES event_spaces(id),

    media_url TEXT NOT NULL,

    media_type VARCHAR(20) NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_event_space_media_event_space_id
ON event_space_media(event_space_id);


ALTER TABLE event_spaces
ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

ALTER TABLE event_spaces
ADD COLUMN highlighted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE event_spaces
ADD COLUMN event_space_type VARCHAR(50);