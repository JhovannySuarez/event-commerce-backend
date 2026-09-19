package com.eventcommerce.venue.repository;

import com.eventcommerce.venue.domain.BookingMode;
import com.eventcommerce.venue.dto.AvailableDate;
import com.eventcommerce.venue.dto.AvailableSlot;
import com.eventcommerce.venue.dto.AvailableSpace;
import com.eventcommerce.venue.dto.VenueSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class VenueSearchRepositoryImpl
        implements VenueSearchRepository {

    private static final String EVENT_SPACE_AVAILABILITY_SQL = """
        SELECT
            v.id AS venue_id,
            v.address AS venue_address,
            v.average_rating,
            v.review_count,
            es.id AS event_space_id,
            es.name AS event_space_name,
            es.description AS event_space_description,
            es.capacity_min,
            es.capacity_max,
            eset.booking_mode,
            eset.minimum_hours,
            oh.operating_from,
            oh.operating_until,
            COALESCE(
                ebc.extension_allowed,
                FALSE
            ) AS extension_allowed,
            ebc.extension_until,
            vce.start_at AS event_start_at,
            vce.end_at AS event_end_at,
            CAST(:eventDate AS date) AS available_date
        FROM venues v
        JOIN event_spaces es
          ON es.venue_id = v.id
         AND es.active = TRUE
        JOIN event_space_event_types eset
          ON eset.event_space_id = es.id
         AND eset.event_type_id = :eventTypeId
         AND eset.active = TRUE
        JOIN event_space_operating_hours oh
          ON oh.event_space_id = es.id
         AND UPPER(oh.day_of_week) =
             UPPER(
                 TRIM(
                     TO_CHAR(
                         CAST(:eventDate AS date),
                         'DAY'
                     )
                 )
             )
        LEFT JOIN event_space_booking_config ebc
          ON ebc.event_space_id = es.id
        LEFT JOIN calendar_event_spaces ces
          ON ces.event_space_id = es.id
         AND ces.active = TRUE
        LEFT JOIN venue_calendar_events vce
          ON vce.id = ces.calendar_event_id
         AND vce.active = TRUE
         AND vce.status IN (
             'TENTATIVE',
             'CONFIRMED',
             'BLOCKED'
         )
         AND vce.start_at <
             (
                 CAST(:eventDate AS date)
                 + INTERVAL '1 day'
             )
         AND vce.end_at >
             CAST(:eventDate AS date)
        WHERE v.active = TRUE
          AND v.city_id = :cityId
          AND es.capacity_max >= :requestedGuests
        """;

    private static final String EVENT_SPACE_Q2_AVAILABILITY_SQL = """
        SELECT
            v.id AS venue_id,
            v.address AS venue_address,
            v.average_rating,
            v.review_count,
            es.id AS event_space_id,
            es.name AS event_space_name,
            es.description AS event_space_description,
            es.capacity_min,
            es.capacity_max,
            eset.booking_mode,
            eset.minimum_hours,
            oh.operating_from,
            oh.operating_until,
            COALESCE(
                ebc.extension_allowed,
                FALSE
            ) AS extension_allowed,
            ebc.extension_until,
            search_dates.available_date,
            vce.start_at AS event_start_at,
            vce.end_at AS event_end_at
        FROM venues v
        JOIN event_spaces es
          ON es.venue_id = v.id
         AND es.active = TRUE
        JOIN event_space_event_types eset
          ON eset.event_space_id = es.id
         AND eset.event_type_id = :eventTypeId
         AND eset.active = TRUE
        CROSS JOIN LATERAL (
            SELECT
                generated_date::date AS available_date
            FROM generate_series(
                CAST(:eventDate AS date) - INTERVAL '15 days',
                CAST(:eventDate AS date) + INTERVAL '15 days',
                INTERVAL '1 day'
            ) AS generated_date
            WHERE generated_date::date <> CAST(:eventDate AS date)
        ) search_dates
        JOIN event_space_operating_hours oh
          ON oh.event_space_id = es.id
         AND UPPER(oh.day_of_week) =
             UPPER(
                 TRIM(
                     TO_CHAR(
                         search_dates.available_date,
                         'DAY'
                     )
                 )
             )
        LEFT JOIN event_space_booking_config ebc
          ON ebc.event_space_id = es.id
        LEFT JOIN calendar_event_spaces ces
          ON ces.event_space_id = es.id
         AND ces.active = TRUE
        LEFT JOIN venue_calendar_events vce
          ON vce.id = ces.calendar_event_id
         AND vce.active = TRUE
         AND vce.status IN (
             'TENTATIVE',
             'CONFIRMED',
             'BLOCKED'
         )
         AND vce.start_at <
             search_dates.available_date
             + INTERVAL '1 day'
         AND vce.end_at >
             search_dates.available_date
        WHERE v.active = TRUE
          AND v.city_id = :cityId
          AND es.capacity_max >= :requestedGuests
        """;

    private static final String EVENT_SPACE_AVAILABILITY_Q3_SQL = """
        SELECT
            v.id AS venue_id,
            v.address AS venue_address,
            v.average_rating,
            v.review_count,
            es.id AS event_space_id,
            es.name AS event_space_name,
            es.description AS event_space_description,
            es.capacity_min,
            es.capacity_max,
            eset.booking_mode,
            eset.minimum_hours,
            oh.operating_from,
            oh.operating_until,
            COALESCE(
                ebc.extension_allowed,
                FALSE
            ) AS extension_allowed,
            ebc.extension_until,
            vce.start_at AS event_start_at,
            vce.end_at AS event_end_at,
            CAST(:eventDate AS date) AS available_date
        FROM venues v
        JOIN event_spaces es
          ON es.venue_id = v.id
         AND es.active = TRUE
        JOIN event_space_event_types eset
          ON eset.event_space_id = es.id
         AND eset.event_type_id = :eventTypeId
         AND eset.active = TRUE
        JOIN event_space_operating_hours oh
          ON oh.event_space_id = es.id
         AND UPPER(oh.day_of_week) =
             UPPER(
                 TRIM(
                     TO_CHAR(
                         CAST(:eventDate AS date),
                         'DAY'
                     )
                 )
             )
        LEFT JOIN event_space_booking_config ebc
          ON ebc.event_space_id = es.id
        LEFT JOIN calendar_event_spaces ces
          ON ces.event_space_id = es.id
         AND ces.active = TRUE
        LEFT JOIN venue_calendar_events vce
          ON vce.id = ces.calendar_event_id
         AND vce.active = TRUE
         AND vce.status IN (
             'TENTATIVE',
             'CONFIRMED',
             'BLOCKED'
         )
         AND vce.start_at <
             CAST(:eventDate AS date) + INTERVAL '1 day'
         AND vce.end_at >
             CAST(:eventDate AS date)
        WHERE v.active = TRUE
          AND v.city_id = :cityId
          AND es.capacity_max >= :minimumCapacity
          AND es.capacity_max < :requestedGuests
        """;

    // ============================================================
    // Q1
    // ============================================================
    private static final String VENUE_PAGE_SQL = """
            SELECT
                v.id AS venue_id,
                v.name AS venue_name,
                v.slug AS venue_slug,
                v.description AS venue_description,
                v.address AS venue_address,
                c.name AS city_name,
                v.latitude,
                v.longitude,
                v.average_rating,
                v.review_count
            FROM venues v
            JOIN cities c
              ON c.id = v.city_id
            WHERE v.active = TRUE
              AND v.id IN (:venueIds)
            ORDER BY
                v.average_rating DESC NULLS LAST,
                v.review_count DESC NULLS LAST,
                v.name ASC
            LIMIT :limit
            OFFSET :offset
            """;

    // ============================================================
    // Q2
    // ============================================================
    private static final String VENUE_SUMMARY_SQL = """
            SELECT
                v.id AS venue_id,
                v.name AS venue_name,
                v.slug AS venue_slug,
                v.description AS venue_description,
                v.address AS venue_address,
                c.name AS city_name,
                v.latitude,
                v.longitude,
                v.average_rating,
                v.review_count
            FROM venues v
            JOIN cities c
              ON c.id = v.city_id
            WHERE v.active = TRUE
              AND v.id IN (:venueIds)
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public VenueSearchRepositoryImpl(
            NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<VenueSearchResult> searchQ1(
            UUID eventTypeId,
            UUID cityId,
            LocalDate eventDate,
            int requestedGuests,
            Pageable pageable) {

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue("eventTypeId", eventTypeId)
                        .addValue("cityId", cityId)
                        .addValue("eventDate", eventDate)
                        .addValue("requestedGuests", requestedGuests);

        /*
         * Retrieve event spaces that could potentially be available
         * for the requested date and capacity.
         *
         * Final availability is calculated in Java because occupied
         * intervals must be merged and the resulting gaps evaluated.
         */
        List<EventSpaceAvailabilityRow> rows =
                jdbcTemplate.query(
                        EVENT_SPACE_AVAILABILITY_SQL,
                        params,
                        this::mapEventSpace
                );

        /*
         * Keep only event spaces that are actually available.
         */
        List<AvailableSpace> availableSpaces =
                buildAvailableSpaces(
                        rows,
                        eventDate
                );

        if (availableSpaces.isEmpty()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    0
            );
        }

        /*
         * Metadata for each event space.
         *
         * EVENT_SPACE_AVAILABILITY_SQL may return multiple rows for the
         * same event space because of existing calendar events, so we
         * keep one representative row per event space.
         */
        Map<UUID, EventSpaceAvailabilityRow> rowsByEventSpace =
                rows.stream()
                        .collect(
                                Collectors.toMap(
                                        EventSpaceAvailabilityRow::eventSpaceId,
                                        Function.identity(),
                                        (first, ignored) -> first,
                                        LinkedHashMap::new
                                )
                        );

        /*
         * Pagination is performed at event-space level.
         */
        int start =
                Math.toIntExact(
                        Math.min(
                                pageable.getOffset(),
                                availableSpaces.size()
                        )
                );

        int end =
                Math.min(
                        start + pageable.getPageSize(),
                        availableSpaces.size()
                );

        List<VenueSearchResult> results =
                availableSpaces
                        .subList(start, end)
                        .stream()
                        .map(space -> {
                            EventSpaceAvailabilityRow row =
                                    rowsByEventSpace.get(
                                            space.eventSpaceId()
                                    );

                            return buildVenueSearchResult(
                                    row,
                                    space
                            );
                        })
                        .toList();

        return new PageImpl<>(
                results,
                pageable,
                availableSpaces.size()
        );
    }

    // ============================================================
    // Availability calculation
    // ============================================================

    @Override
    public List<VenueSearchResult> searchQ2(
            UUID cityId,
            UUID eventTypeId,
            int requestedGuests,
            LocalDate eventDate,
            int limit,
            int offset) {

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue("cityId", cityId)
                        .addValue("eventTypeId", eventTypeId)
                        .addValue("requestedGuests", requestedGuests)
                        .addValue("eventDate", eventDate);

        /*
         * Retrieve candidate event spaces for the Q2 date window.
         *
         * Q2:
         * - excludes the exact requested date
         * - searches +/- 15 days
         * - keeps capacity strict:
         *   capacity_max >= requestedGuests
         */
        List<EventSpaceAvailabilityRow> rows =
                jdbcTemplate.query(
                        EVENT_SPACE_Q2_AVAILABILITY_SQL,
                        params,
                        this::mapEventSpace
                );

        if (rows.isEmpty()) {
            return List.of();
        }

        /*
         * Calculate actual availability independently for each
         * event space and date.
         *
         * Result:
         *
         * eventSpaceId -> date -> AvailableSpace
         */
        Map<UUID, Map<LocalDate, AvailableSpace>> availabilityByEventSpace =
                buildAvailabilityByEventSpace(rows);

        if (availabilityByEventSpace.isEmpty()) {
            return List.of();
        }

        /*
         * Keep one representative row per event space.
         * Metadata such as venue, address and rating is the same
         * for all rows belonging to the same event space.
         */
        Map<UUID, EventSpaceAvailabilityRow> rowsByEventSpace =
                rows.stream()
                        .collect(
                                Collectors.toMap(
                                        EventSpaceAvailabilityRow::eventSpaceId,
                                        Function.identity(),
                                        (first, ignored) -> first,
                                        LinkedHashMap::new
                                )
                        );

        /*
         * Sort event spaces by:
         *
         * 1. Closest available date to requested date
         * 2. Venue rating DESC
         * 3. Venue review count DESC
         * 4. Event space name ASC
         */
        List<UUID> orderedEventSpaceIds =
                availabilityByEventSpace.keySet()
                        .stream()
                        .sorted(
                                Comparator
                                        .comparingLong(
                                                (UUID eventSpaceId) ->
                                                        closestDateDistance(
                                                                availabilityByEventSpace
                                                                        .get(eventSpaceId)
                                                                        .keySet(),
                                                                eventDate
                                                        )
                                        )
                                        .thenComparing(
                                                eventSpaceId ->
                                                        rowsByEventSpace
                                                                .get(eventSpaceId)
                                                                .averageRating(),
                                                Comparator.nullsLast(
                                                        Comparator.reverseOrder()
                                                )
                                        )
                                        .thenComparing(
                                                eventSpaceId ->
                                                        rowsByEventSpace
                                                                .get(eventSpaceId)
                                                                .reviewCount(),
                                                Comparator.nullsLast(
                                                        Comparator.reverseOrder()
                                                )
                                        )
                                        .thenComparing(
                                                eventSpaceId ->
                                                        rowsByEventSpace
                                                                .get(eventSpaceId)
                                                                .eventSpaceName(),
                                                Comparator.nullsLast(
                                                        Comparator.naturalOrder()
                                                )
                                        )
                                        .thenComparing(UUID::toString)
                        )
                        .skip(offset)
                        .limit(limit)
                        .toList();

        return orderedEventSpaceIds.stream()
                .map(eventSpaceId -> {

                    EventSpaceAvailabilityRow row =
                            rowsByEventSpace.get(eventSpaceId);

                    Map<LocalDate, AvailableSpace> dates =
                            availabilityByEventSpace.get(eventSpaceId);

                    List<AvailableDate> availableDates =
                            buildAvailableDatesForEventSpace(
                                    dates,
                                    eventDate
                            );

                    return new VenueSearchResult(
                            row.eventSpaceId(),
                            row.eventSpaceName(),
                            row.venueId(),
                            row.description(),
                            row.address(),
                            row.averageRating(),
                            row.reviewCount(),
                            row.capacityMin(),
                            row.capacityMax(),
                            row.bookingMode(),
                            row.minimumHours(),
                            availableDates
                    );
                })
                .toList();
    }

    @Override
    public List<VenueSearchResult> searchQ3(
            UUID cityId,
            UUID eventTypeId,
            int requestedGuests,
            LocalDate eventDate,
            int limit,
            int offset) {

        int minimumCapacity =
                (int) Math.ceil(requestedGuests * 0.90);

        LocalDate fromDate =
                eventDate.minusDays(15);

        LocalDate toDate =
                eventDate.plusDays(15);

        /*
         * Q3:
         *
         * - searches +/- 15 days, including the requested date
         * - allows event spaces with capacity up to 10% below
         *   the requested number of guests
         *
         * minimumCapacity <= capacity_max < requestedGuests
         */
        Map<UUID, Map<LocalDate, AvailableSpace>>
                availabilityByEventSpace =
                new LinkedHashMap<>();

        /*
         * Keep one representative row per event space.
         *
         * This provides the Event Space / Venue metadata required
         * to build VenueSearchResult.
         */
        Map<UUID, EventSpaceAvailabilityRow> rowsByEventSpace =
                new LinkedHashMap<>();

        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {

            MapSqlParameterSource params =
                    new MapSqlParameterSource()
                            .addValue("cityId", cityId)
                            .addValue("eventTypeId", eventTypeId)
                            .addValue(
                                    "minimumCapacity",
                                    minimumCapacity
                            )
                            .addValue(
                                    "requestedGuests",
                                    requestedGuests
                            )
                            .addValue(
                                    "eventDate",
                                    currentDate
                            );

            List<EventSpaceAvailabilityRow> rows =
                    jdbcTemplate.query(
                            EVENT_SPACE_AVAILABILITY_Q3_SQL,
                            params,
                            this::mapEventSpace
                    );

            if (!rows.isEmpty()) {

                /*
                 * Preserve one row per Event Space for metadata.
                 */
                rows.forEach(
                        row ->
                                rowsByEventSpace.putIfAbsent(
                                        row.eventSpaceId(),
                                        row
                                )
                );

                /*
                 * Calculate actual availability for this date.
                 */
                List<AvailableSpace> availableSpaces =
                        buildAvailableSpaces(
                                rows,
                                currentDate
                        );

                for (AvailableSpace space : availableSpaces) {

                    availabilityByEventSpace
                            .computeIfAbsent(
                                    space.eventSpaceId(),
                                    ignored -> new TreeMap<>()
                            )
                            .put(
                                    currentDate,
                                    space
                            );
                }
            }

            currentDate =
                    currentDate.plusDays(1);
        }

        if (availabilityByEventSpace.isEmpty()) {
            return List.of();
        }

        /*
         * Sort Event Spaces by:
         *
         * 1. Closest available date
         * 2. Venue rating DESC
         * 3. Venue review count DESC
         * 4. Event Space name ASC
         * 5. Event Space id ASC
         */
        List<UUID> orderedEventSpaceIds =
                availabilityByEventSpace.keySet()
                        .stream()
                        .sorted(
                                Comparator
                                        .comparingLong(
                                                (UUID eventSpaceId) ->
                                                        closestDateDistance(
                                                                availabilityByEventSpace
                                                                        .get(eventSpaceId)
                                                                        .keySet(),
                                                                eventDate
                                                        )
                                        )
                                        .thenComparing(
                                                eventSpaceId ->
                                                        rowsByEventSpace
                                                                .get(eventSpaceId)
                                                                .averageRating(),
                                                Comparator.nullsLast(
                                                        Comparator.reverseOrder()
                                                )
                                        )
                                        .thenComparing(
                                                eventSpaceId ->
                                                        rowsByEventSpace
                                                                .get(eventSpaceId)
                                                                .reviewCount(),
                                                Comparator.nullsLast(
                                                        Comparator.reverseOrder()
                                                )
                                        )
                                        .thenComparing(
                                                eventSpaceId ->
                                                        rowsByEventSpace
                                                                .get(eventSpaceId)
                                                                .eventSpaceName(),
                                                Comparator.nullsLast(
                                                        Comparator.naturalOrder()
                                                )
                                        )
                                        .thenComparing(UUID::toString)
                        )
                        .skip(offset)
                        .limit(limit)
                        .toList();

        /*
         * Build the final Event Space results.
         */
        return orderedEventSpaceIds.stream()
                .map(eventSpaceId -> {

                    EventSpaceAvailabilityRow row =
                            rowsByEventSpace.get(eventSpaceId);

                    Map<LocalDate, AvailableSpace> dates =
                            availabilityByEventSpace.get(eventSpaceId);

                    List<AvailableDate> availableDates =
                            buildAvailableDatesForEventSpace(
                                    dates,
                                    eventDate
                            );

                    return new VenueSearchResult(
                            row.eventSpaceId(),
                            row.eventSpaceName(),
                            row.venueId(),
                            row.description(),
                            row.address(),
                            row.averageRating(),
                            row.reviewCount(),
                            row.capacityMin(),
                            row.capacityMax(),
                            row.bookingMode(),
                            row.minimumHours(),
                            availableDates
                    );
                })
                .toList();
    }

    private VenueSearchResult buildVenueSearchResult(
            EventSpaceAvailabilityRow row,
            AvailableSpace space) {

        AvailableDate availableDate =
                new AvailableDate(
                        space.availableDate(),
                        space.availableHours(),
                        space.availableSlots()
                );

        return new VenueSearchResult(
                space.eventSpaceId(),
                space.name(),
                row.venueId(),
                row.description(),
                row.address(),
                row.averageRating(),
                row.reviewCount(),
                space.capacityMin(),
                space.capacityMax(),
                space.bookingMode(),
                space.minimumHours(),
                List.of(availableDate)
        );
    }

    private long closestDateDistance(
            Set<LocalDate> availableDates,
            LocalDate requestedDate) {

        return availableDates.stream()
                .mapToLong(
                        date ->
                                Math.abs(
                                        ChronoUnit.DAYS.between(
                                                requestedDate,
                                                date
                                        )
                                )
                )
                .min()
                .orElse(Long.MAX_VALUE);
    }

    private EventSpaceAvailabilityRow mapEventSpace(
            ResultSet rs,
            int rowNum) throws SQLException {

        return new EventSpaceAvailabilityRow(
                rs.getObject(
                        "venue_id",
                        UUID.class
                ),
                rs.getString(
                        "venue_address"
                ),
                rs.getObject(
                        "average_rating",
                        Double.class
                ),
                rs.getObject(
                        "review_count",
                        Integer.class
                ),
                rs.getObject(
                        "event_space_id",
                        UUID.class
                ),
                rs.getString(
                        "event_space_name"
                ),
                rs.getString(
                        "event_space_description"
                ),
                rs.getObject(
                        "capacity_min",
                        Integer.class
                ),
                rs.getObject(
                        "capacity_max",
                        Integer.class
                ),
                rs.getString(
                        "booking_mode"
                ),
                rs.getObject(
                        "minimum_hours",
                        Integer.class
                ),
                rs.getObject(
                        "operating_from",
                        LocalTime.class
                ),
                rs.getObject(
                        "operating_until",
                        LocalTime.class
                ),
                rs.getObject(
                        "extension_allowed",
                        Boolean.class
                ),
                rs.getObject(
                        "extension_until",
                        LocalTime.class
                ),
                rs.getObject(
                        "event_start_at",
                        LocalDateTime.class
                ),
                rs.getObject(
                        "event_end_at",
                        LocalDateTime.class
                ),
                rs.getObject(
                        "available_date",
                        LocalDate.class
                )
        );
    }

    private List<AvailableSpace> buildAvailableSpaces(
            List<EventSpaceAvailabilityRow> rows,
            LocalDate eventDate) {

        Map<UUID, List<EventSpaceAvailabilityRow>> grouped =
                rows.stream()
                        .collect(
                                Collectors.groupingBy(
                                        EventSpaceAvailabilityRow::eventSpaceId,
                                        LinkedHashMap::new,
                                        Collectors.toList()
                                )
                        );

        List<AvailableSpace> result = new ArrayList<>();

        for (List<EventSpaceAvailabilityRow> spaceRows : grouped.values()) {

            EventSpaceAvailabilityRow first = spaceRows.getFirst();

            AvailableSpace availableSpace =
                    buildAvailableSpace(
                            spaceRows,
                            first,
                            eventDate
                    );

            if (availableSpace != null) {
                result.add(availableSpace);
            }
        }

        return result;
    }

    private AvailableSpace buildAvailableSpace(
            List<EventSpaceAvailabilityRow> spaceRows,
            EventSpaceAvailabilityRow first,
            LocalDate eventDate) {

        if (BookingMode.FULL_DAY.name()
                .equalsIgnoreCase(first.bookingMode())) {

            return buildFullDayAvailableSpace(
                    spaceRows,
                    first
            );
        }

        return buildHourlyAvailableSpace(
                spaceRows,
                first,
                eventDate
        );
    }

    private AvailableSpace buildFullDayAvailableSpace(
            List<EventSpaceAvailabilityRow> spaceRows,
            EventSpaceAvailabilityRow first) {

        boolean occupied =
                spaceRows.stream()
                        .anyMatch(
                                row -> row.eventStartAt() != null
                        );

        if (occupied) {
            return null;
        }

        return new AvailableSpace(
                first.eventSpaceId(),
                first.eventSpaceName(),
                first.capacityMin(),
                first.capacityMax(),
                first.bookingMode(),
                null,
                null,
                List.of(),
                first.availableDate()
        );
    }

    private AvailableSpace buildHourlyAvailableSpace(
            List<EventSpaceAvailabilityRow> spaceRows,
            EventSpaceAvailabilityRow first,
            LocalDate eventDate) {

        List<AvailableSlot> slots =
                calculateHourlySlots(
                        spaceRows,
                        eventDate
                );

        if (slots.isEmpty()) {
            return null;
        }

        long availableHours =
                slots.stream()
                        .mapToLong(AvailableSlot::hours)
                        .sum();

        return new AvailableSpace(
                first.eventSpaceId(),
                first.eventSpaceName(),
                first.capacityMin(),
                first.capacityMax(),
                first.bookingMode(),
                first.minimumHours(),
                availableHours,
                slots,
                first.availableDate()
        );
    }

    private List<AvailableSlot> calculateHourlySlots(
            List<EventSpaceAvailabilityRow> rows,
            LocalDate eventDate) {

        EventSpaceAvailabilityRow config = rows.getFirst();

        TimeInterval operatingWindow =
                buildOperatingWindow(
                        config,
                        eventDate
                );

        List<TimeInterval> occupiedIntervals =
                getMergedOccupiedIntervals(
                        rows,
                        operatingWindow
                );

        return findAvailableSlots(
                operatingWindow,
                occupiedIntervals,
                config.minimumHours()
        );
    }

    private TimeInterval buildOperatingWindow(
            EventSpaceAvailabilityRow config,
            LocalDate eventDate) {

        LocalTime operatingFrom =
                config.operatingFrom();

        LocalTime availabilityUntil =
                config.operatingUntil();

        if (Boolean.TRUE.equals(config.extensionAllowed())
                && config.extensionUntil() != null) {

            availabilityUntil =
                    config.extensionUntil();
        }

        LocalDateTime windowStart =
                LocalDateTime.of(
                        eventDate,
                        operatingFrom
                );

        LocalDateTime windowEnd =
                LocalDateTime.of(
                        eventDate,
                        availabilityUntil
                ).plusDays(
                        availabilityUntil.isAfter(operatingFrom)
                                ? 0
                                : 1
                );

        return new TimeInterval(
                windowStart,
                windowEnd
        );
    }

    private List<TimeInterval> getMergedOccupiedIntervals(
            List<EventSpaceAvailabilityRow> rows,
            TimeInterval operatingWindow) {

        List<TimeInterval> occupied =
                rows.stream()
                        .filter(row -> row.eventStartAt() != null)
                        .map(
                                row ->
                                        new TimeInterval(
                                                max(
                                                        row.eventStartAt(),
                                                        operatingWindow.start()
                                                ),
                                                min(
                                                        row.eventEndAt(),
                                                        operatingWindow.end()
                                                )
                                        )
                        )
                        .filter(
                                interval ->
                                        interval.start()
                                                .isBefore(
                                                        interval.end()
                                                )
                        )
                        .sorted(
                                Comparator.comparing(
                                        TimeInterval::start
                                )
                        )
                        .toList();

        return mergeIntervals(occupied);
    }

    private List<AvailableSlot> findAvailableSlots(
            TimeInterval operatingWindow,
            List<TimeInterval> occupiedIntervals,
            Integer minimumHours) {

        List<AvailableSlot> slots =
                new ArrayList<>();

        LocalDateTime cursor =
                operatingWindow.start();

        for (TimeInterval interval : occupiedIntervals) {

            if (cursor.isBefore(interval.start())) {

                addIfValidSlot(
                        slots,
                        cursor,
                        interval.start(),
                        minimumHours
                );
            }

            if (interval.end().isAfter(cursor)) {
                cursor = interval.end();
            }
        }

        if (cursor.isBefore(operatingWindow.end())) {

            addIfValidSlot(
                    slots,
                    cursor,
                    operatingWindow.end(),
                    minimumHours
            );
        }

        return slots;
    }

    private void addIfValidSlot(
            List<AvailableSlot> slots,
            LocalDateTime start,
            LocalDateTime end,
            Integer minimumHours) {

        if (minimumHours == null) {
            return;
        }

        long minutes =
                Duration.between(
                        start,
                        end
                ).toMinutes();

        if (minutes < minimumHours * 60L) {
            return;
        }

        slots.add(
                new AvailableSlot(
                        start.toLocalTime(),
                        end.toLocalTime(),
                        minutes / 60
                )
        );
    }

    private List<TimeInterval> mergeIntervals(
            List<TimeInterval> intervals) {

        if (intervals.isEmpty()) {
            return List.of();
        }

        List<TimeInterval> merged =
                new ArrayList<>();

        TimeInterval current =
                intervals.getFirst();

        for (int i = 1;
             i < intervals.size();
             i++) {

            TimeInterval next =
                    intervals.get(i);

            if (!next.start().isAfter(
                    current.end())) {

                current =
                        new TimeInterval(
                                current.start(),
                                max(
                                        current.end(),
                                        next.end()
                                )
                        );

            } else {

                merged.add(current);
                current = next;
            }
        }

        merged.add(current);

        return merged;
    }

    private LocalDateTime max(
            LocalDateTime first,
            LocalDateTime second) {

        return first.isAfter(second)
                ? first
                : second;
    }

    private LocalDateTime min(
            LocalDateTime first,
            LocalDateTime second) {

        return first.isBefore(second)
                ? first
                : second;
    }

    private Map<UUID, Map<LocalDate, AvailableSpace>>
    buildAvailabilityByEventSpace(
            List<EventSpaceAvailabilityRow> rows) {

        Map<UUID, Map<LocalDate, AvailableSpace>> result =
                new LinkedHashMap<>();

        Map<LocalDate, List<EventSpaceAvailabilityRow>> rowsByDate =
                rows.stream()
                        .collect(
                                Collectors.groupingBy(
                                        EventSpaceAvailabilityRow::availableDate,
                                        TreeMap::new,
                                        Collectors.toList()
                                )
                        );

        for (Map.Entry<LocalDate, List<EventSpaceAvailabilityRow>> entry
                : rowsByDate.entrySet()) {

            LocalDate availableDate = entry.getKey();

            List<AvailableSpace> availableSpaces =
                    buildAvailableSpaces(
                            entry.getValue(),
                            availableDate
                    );

            for (AvailableSpace space : availableSpaces) {
                result.computeIfAbsent(
                                space.eventSpaceId(),
                                ignored -> new TreeMap<>()
                        )
                        .put(
                                availableDate,
                                space
                        );
            }
        }

        return result;
    }

    private List<AvailableDate> buildAvailableDatesForEventSpace(
            Map<LocalDate, AvailableSpace> dates,
            LocalDate requestedDate) {

        return dates.entrySet()
                .stream()
                .sorted(
                        Comparator
                                .comparingLong(
                                        (Map.Entry<LocalDate, AvailableSpace> entry) ->
                                                Math.abs(
                                                        ChronoUnit.DAYS.between(
                                                                requestedDate,
                                                                entry.getKey()
                                                        )
                                                )
                                )
                                .thenComparing(Map.Entry::getKey)
                )
                .map(entry -> {

                    AvailableSpace space = entry.getValue();

                    return new AvailableDate(
                            entry.getKey(),
                            space.availableHours(),
                            space.availableSlots()
                    );
                })
                .toList();
    }

    private record EventSpaceAvailabilityRow(
            UUID venueId,
            String address,
            Double averageRating,
            Integer reviewCount,
            UUID eventSpaceId,
            String eventSpaceName,
            String description,
            Integer capacityMin,
            Integer capacityMax,
            String bookingMode,
            Integer minimumHours,
            LocalTime operatingFrom,
            LocalTime operatingUntil,
            Boolean extensionAllowed,
            LocalTime extensionUntil,
            LocalDateTime eventStartAt,
            LocalDateTime eventEndAt,
            LocalDate availableDate
    ) {
    }

    private record TimeInterval(
            LocalDateTime start,
            LocalDateTime end
    ) {
    }

}