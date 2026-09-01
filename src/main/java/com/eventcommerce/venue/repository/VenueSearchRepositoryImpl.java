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
import java.util.stream.Collectors;

@Repository
public class VenueSearchRepositoryImpl
        implements VenueSearchRepository {

    private static final String EVENT_SPACE_AVAILABILITY_SQL = """
            SELECT
                v.id AS venue_id,
                es.id AS event_space_id,
                es.name AS event_space_name,
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
                es.id AS event_space_id,
                es.name AS event_space_name,
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
                es.id AS event_space_id,
                es.name AS event_space_name,
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
              /*
               * Q3 allows up to 10% below the requested capacity.
               */
              AND es.capacity_max >= :minimumCapacity
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

    // ============================================================
    // Helpers
    // ============================================================
    private static final String COUNT_AVAILABLE_VENUES_SQL = """
            SELECT COUNT(*)
            FROM venues v
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
         * Retrieve all event spaces that could potentially be
         * available for the requested date.
         *
         * Availability is calculated in Java because we need to
         * merge occupied intervals and calculate the resulting gaps.
         */
        List<EventSpaceAvailabilityRow> spaces =
                jdbcTemplate.query(
                        EVENT_SPACE_AVAILABILITY_SQL,
                        params,
                        this::mapEventSpace
                );

        Map<UUID, List<AvailableSpace>> spacesByVenue =
                buildAvailableSpaces(
                        spaces,
                        eventDate
                );

        if (spacesByVenue.isEmpty()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    0
            );
        }

        /*
         * Only venues with at least one available event space
         * participate in pagination.
         */
        List<UUID> availableVenueIds =
                new ArrayList<>(
                        spacesByVenue.keySet()
                );

        long total =
                countAvailableVenues(
                        availableVenueIds
                );

        if (total == 0) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    0
            );
        }

        /*
         * Pagination happens at venue level.
         */
        MapSqlParameterSource venueParams =
                new MapSqlParameterSource()
                        .addValue(
                                "venueIds",
                                availableVenueIds
                        )
                        .addValue(
                                "limit",
                                pageable.getPageSize()
                        )
                        .addValue(
                                "offset",
                                pageable.getOffset()
                        );

        List<VenueRow> venues =
                jdbcTemplate.query(
                        VENUE_PAGE_SQL,
                        venueParams,
                        this::mapVenue
                );

        List<VenueSearchResult> results =
                venues.stream()
                        .map(venue ->
                                new VenueSearchResult(
                                        venue.venueId(),
                                        venue.name(),
                                        venue.slug(),
                                        venue.description(),
                                        venue.address(),
                                        venue.city(),
                                        venue.latitude(),
                                        venue.longitude(),
                                        venue.averageRating(),
                                        venue.reviewCount(),
                                        spacesByVenue.get(
                                                venue.venueId()
                                        ),
                                        List.of()
                                )
                        )
                        .toList();

        return new PageImpl<>(
                results,
                pageable,
                total
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
            int offset,
            Set<UUID> excludedVenueIds) {

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue("cityId", cityId)
                        .addValue("eventTypeId", eventTypeId)
                        .addValue("requestedGuests", requestedGuests)
                        .addValue("eventDate", eventDate);

        /*
         * Retrieve all candidate event spaces for the Q2 date window.
         *
         * Q2 keeps capacity strict:
         *
         * capacity_max >= requestedGuests
         *
         * but allows availability within +/- 15 days.
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
         * Build:
         *
         * date -> venue -> available spaces
         */
        Map<LocalDate, Map<UUID, List<AvailableSpace>>>
                availabilityByDate =
                buildAvailabilityByDate(rows);

        if (availabilityByDate.isEmpty()) {
            return List.of();
        }

        /*
         * Transform:
         *
         * date -> venue -> spaces
         *
         * into:
         *
         * venue -> date -> spaces
         *
         * Also exclude venues already returned by previous tiers.
         */
        Map<UUID, Map<LocalDate, List<AvailableSpace>>>
                availableDatesByVenue =
                groupAvailabilityByVenue(
                        availabilityByDate,
                        excludedVenueIds
                );

        if (availableDatesByVenue.isEmpty()) {
            return List.of();
        }

        /*
         * Load venue information.
         */
        MapSqlParameterSource venueParams =
                new MapSqlParameterSource()
                        .addValue(
                                "venueIds",
                                new ArrayList<>(
                                        availableDatesByVenue.keySet()
                                )
                        );

        List<VenueRow> venueRows =
                jdbcTemplate.query(
                        VENUE_SUMMARY_SQL,
                        venueParams,
                        this::mapVenue
                );

        /*
         * Sort according to the common venue ordering rules:
         *
         * 1. Closest available date
         * 2. Rating DESC
         * 3. Review count DESC
         * 4. Name ASC
         */
        List<VenueRow> orderedVenues =
                sortVenues(
                        venueRows,
                        availableDatesByVenue,
                        eventDate
                );

        /*
         * Pagination happens at venue level.
         *
         * We never paginate individual dates.
         */
        List<VenueRow> pageVenues =
                orderedVenues.stream()
                        .skip(offset)
                        .limit(limit)
                        .toList();

        if (pageVenues.isEmpty()) {
            return List.of();
        }

        /*
         * Build the final Q2 response.
         *
         * Each venue contains all available dates
         * within the Q2 +/- 15 day window.
         */
        return pageVenues.stream()
                .map(
                        venue -> {

                            Map<LocalDate, List<AvailableSpace>> dates =
                                    availableDatesByVenue.get(
                                            venue.venueId()
                                    );

                            List<AvailableDate> availableDates =
                                    buildAvailableDates(
                                            dates,
                                            eventDate
                                    );

                            return new VenueSearchResult(
                                    venue.venueId(),
                                    venue.name(),
                                    venue.slug(),
                                    venue.description(),
                                    venue.address(),
                                    venue.city(),
                                    venue.latitude(),
                                    venue.longitude(),
                                    venue.averageRating(),
                                    venue.reviewCount(),
                                    List.of(),
                                    availableDates
                            );
                        }
                )
                .toList();
    }

    @Override
    public List<VenueSearchResult> searchQ3(
            UUID cityId,
            UUID eventTypeId,
            int requestedGuests,
            LocalDate eventDate,
            int limit,
            int offset,
            Set<UUID> excludedVenueIds) {

        int minimumCapacity =
                (int) Math.ceil(requestedGuests * 0.90);

        LocalDate fromDate =
                eventDate.minusDays(15);

        LocalDate toDate =
                eventDate.plusDays(15);

        /*
         * Build:
         *
         * date -> venue -> available spaces
         *
         * Availability is calculated independently for each
         * date in the Q3 +/- 15 day window.
         */
        Map<LocalDate, Map<UUID, List<AvailableSpace>>>
                availabilityByDate =
                new TreeMap<>();

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

                Map<UUID, List<AvailableSpace>> spacesByVenue =
                        buildAvailableSpaces(
                                rows,
                                currentDate
                        );

                if (!spacesByVenue.isEmpty()) {
                    availabilityByDate.put(
                            currentDate,
                            spacesByVenue
                    );
                }
            }

            currentDate =
                    currentDate.plusDays(1);
        }

        if (availabilityByDate.isEmpty()) {
            return List.of();
        }

        /*
         * Transform
         * date -> venue -> spaces
         * into:
         * venue -> date -> spaces
         * Also exclude venues already returned by Q1/Q2.
         */
        Map<UUID, Map<LocalDate, List<AvailableSpace>>>
                availableDatesByVenue =
                groupAvailabilityByVenue(
                        availabilityByDate,
                        excludedVenueIds
                );

        if (availableDatesByVenue.isEmpty()) {
            return List.of();
        }

        /*
         * Load venue information.
         */
        MapSqlParameterSource venueParams =
                new MapSqlParameterSource()
                        .addValue(
                                "venueIds",
                                new ArrayList<>(
                                        availableDatesByVenue.keySet()
                                )
                        );

        List<VenueRow> venueRows =
                jdbcTemplate.query(
                        VENUE_SUMMARY_SQL,
                        venueParams,
                        this::mapVenue
                );

        /*
         * Sort according to the common venue ordering rules:
         *
         * 1. Closest available date
         * 2. Rating DESC
         * 3. Review count DESC
         * 4. Name ASC
         */
        List<VenueRow> orderedVenues =
                sortVenues(
                        venueRows,
                        availableDatesByVenue,
                        eventDate
                );

        /*
         * Pagination happens at venue level.
         *
         * We never paginate individual dates.
         */
        List<VenueRow> pageVenues =
                orderedVenues.stream()
                        .skip(offset)
                        .limit(limit)
                        .toList();

        if (pageVenues.isEmpty()) {
            return List.of();
        }

        /*
         * Build the final Q3 response.
         *
         * Each venue contains all available dates
         * within the Q3 +/- 15 day window.
         */
        return pageVenues.stream()
                .map(
                        venue ->
                                buildVenueSearchResult(
                                        venue,
                                        availableDatesByVenue.get(
                                                venue.venueId()
                                        ),
                                        eventDate
                                )
                )
                .toList();
    }

    private Map<LocalDate, Map<UUID, List<AvailableSpace>>> buildAvailabilityByDate(
            List<EventSpaceAvailabilityRow> rows) {

        return rows.stream()
                .collect(
                        Collectors.groupingBy(
                                EventSpaceAvailabilityRow::availableDate,
                                TreeMap::new,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        dateRows ->
                                                buildAvailableSpaces(
                                                        dateRows,
                                                        dateRows.getFirst().availableDate()
                                                )
                                )
                        )
                );
    }

    private Map<UUID, Map<LocalDate, List<AvailableSpace>>> groupAvailabilityByVenue(
            Map<LocalDate, Map<UUID, List<AvailableSpace>>> availabilityByDate,
            Set<UUID> excludedVenueIds) {

        Map<UUID, Map<LocalDate, List<AvailableSpace>>> result =
                new LinkedHashMap<>();

        for (Map.Entry<LocalDate, Map<UUID, List<AvailableSpace>>> dateEntry
                : availabilityByDate.entrySet()) {

            LocalDate availableDate = dateEntry.getKey();

            for (Map.Entry<UUID, List<AvailableSpace>> venueEntry
                    : dateEntry.getValue().entrySet()) {

                UUID venueId = venueEntry.getKey();
                List<AvailableSpace> spaces = venueEntry.getValue();

                if (spaces.isEmpty()
                        || excludedVenueIds != null
                        && excludedVenueIds.contains(venueId)) {
                    continue;
                }

                result.computeIfAbsent(
                        venueId,
                        ignored -> new TreeMap<>()
                ).put(
                        availableDate,
                        spaces
                );
            }
        }

        return result;
    }

    private List<VenueRow> sortVenues(
            List<VenueRow> venues,
            Map<UUID, Map<LocalDate, List<AvailableSpace>>> availabilityByVenue,
            LocalDate requestedDate) {

        return venues.stream()
                .filter(
                        venue ->
                                availabilityByVenue.containsKey(
                                        venue.venueId()
                                )
                )
                .sorted(
                        Comparator
                                .comparing(
                                        (VenueRow venue) ->
                                                closestDateDistance(
                                                        availabilityByVenue.get(
                                                                venue.venueId()
                                                        ),
                                                        requestedDate
                                                )
                                )
                                .thenComparing(
                                        VenueRow::averageRating,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        VenueRow::reviewCount,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        VenueRow::name,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                )
                .toList();
    }

    private List<AvailableDate> buildAvailableDates(
            Map<LocalDate, List<AvailableSpace>> dates,
            LocalDate requestedDate) {

        return dates.entrySet()
                .stream()
                .sorted(
                        Map.Entry.comparingByKey(
                                Comparator.comparingLong(
                                        date ->
                                                Math.abs(
                                                        ChronoUnit.DAYS.between(
                                                                requestedDate,
                                                                date
                                                        )
                                                )
                                )
                        )
                )
                .map(
                        entry ->
                                new AvailableDate(
                                        entry.getKey(),
                                        entry.getValue()
                                )
                )
                .toList();
    }

    private VenueSearchResult buildVenueSearchResult(
            VenueRow venue,
            Map<LocalDate, List<AvailableSpace>> dates,
            LocalDate requestedDate) {

        return new VenueSearchResult(
                venue.venueId(),
                venue.name(),
                venue.slug(),
                venue.description(),
                venue.address(),
                venue.city(),
                venue.latitude(),
                venue.longitude(),
                venue.averageRating(),
                venue.reviewCount(),
                List.of(),
                buildAvailableDates(
                        dates,
                        requestedDate
                )
        );
    }

    private long closestDateDistance(
            Map<LocalDate, List<AvailableSpace>> dates,
            LocalDate requestedDate) {

        return dates.keySet()
                .stream()
                .mapToLong(
                        date ->
                                Math.abs(
                                        java.time.temporal.ChronoUnit
                                                .DAYS
                                                .between(
                                                        requestedDate,
                                                        date
                                                )
                                )
                )
                .min()
                .orElse(Long.MAX_VALUE);
    }

    private long countAvailableVenues(
            List<UUID> availableVenueIds) {

        if (availableVenueIds.isEmpty()) {
            return 0;
        }

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue(
                                "venueIds",
                                availableVenueIds
                        );

        Long count =
                jdbcTemplate.queryForObject(
                        COUNT_AVAILABLE_VENUES_SQL,
                        params,
                        Long.class
                );

        return count == null ? 0 : count;
    }

    private VenueRow mapVenue(
            ResultSet rs,
            int rowNum) throws SQLException {

        return new VenueRow(
                rs.getObject(
                        "venue_id",
                        UUID.class
                ),
                rs.getString(
                        "venue_name"
                ),
                rs.getString(
                        "venue_slug"
                ),
                rs.getString(
                        "venue_description"
                ),
                rs.getString(
                        "venue_address"
                ),
                rs.getString(
                        "city_name"
                ),
                rs.getObject(
                        "latitude",
                        Double.class
                ),
                rs.getObject(
                        "longitude",
                        Double.class
                ),
                rs.getObject(
                        "average_rating",
                        Double.class
                ),
                rs.getObject(
                        "review_count",
                        Integer.class
                )
        );
    }

    private EventSpaceAvailabilityRow mapEventSpace(
            ResultSet rs,
            int rowNum) throws SQLException {

        return new EventSpaceAvailabilityRow(
                rs.getObject(
                        "venue_id",
                        UUID.class
                ),
                rs.getObject(
                        "event_space_id",
                        UUID.class
                ),
                rs.getString(
                        "event_space_name"
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

    private Map<UUID, List<AvailableSpace>> buildAvailableSpaces(
            List<EventSpaceAvailabilityRow> rows,
            LocalDate eventDate) {

        Map<UUID, List<AvailableSpace>> result =
                new LinkedHashMap<>();

        Map<UUID, List<EventSpaceAvailabilityRow>> grouped =
                rows.stream()
                        .collect(
                                Collectors.groupingBy(
                                        EventSpaceAvailabilityRow::eventSpaceId,
                                        LinkedHashMap::new,
                                        Collectors.toList()
                                )
                        );

        for (List<EventSpaceAvailabilityRow> spaceRows : grouped.values()) {

            EventSpaceAvailabilityRow first = spaceRows.getFirst();

            AvailableSpace availableSpace =
                    buildAvailableSpace(
                            spaceRows,
                            first,
                            eventDate
                    );

            if (availableSpace == null) {
                continue;
            }

            result.computeIfAbsent(
                    first.venueId(),
                    ignored -> new ArrayList<>()
            ).add(availableSpace);
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

    // ============================================================
    // Internal records
    // ============================================================

    private record VenueRow(
            UUID venueId,
            String name,
            String slug,
            String description,
            String address,
            String city,
            Double latitude,
            Double longitude,
            Double averageRating,
            Integer reviewCount
    ) {
    }

    private record EventSpaceAvailabilityRow(
            UUID venueId,
            UUID eventSpaceId,
            String eventSpaceName,
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