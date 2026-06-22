ALTER TABLE venue_calendar_events
ADD COLUMN event_display_name VARCHAR(200),
ADD COLUMN guest_count INTEGER;

ALTER TABLE venue_calendar_events
ADD CONSTRAINT chk_calendar_events_guest_count
CHECK (guest_count IS NULL OR guest_count > 0);