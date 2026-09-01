CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_calendar_event_spaces_active_space_id
ON calendar_event_spaces (event_space_id, calendar_event_id)
WHERE active = TRUE;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_vce_source_active
ON venue_calendar_events (source, id)
WHERE active = TRUE;