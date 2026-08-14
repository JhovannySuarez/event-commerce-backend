package com.eventcommerce.location.repository;

import java.util.List;
import java.util.UUID;

import com.eventcommerce.location.domain.City;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends CrudRepository<City, UUID> {

    @Query("""
            SELECT DISTINCT
                c.id AS id,
                c.name AS name,
                s.name AS state,
                co.name AS country
            FROM cities c
            JOIN states s
                ON s.id = c.state_id
            JOIN countries co
                ON co.id = s.country_id
            JOIN venues v
                ON v.city_id = c.id
            WHERE v.active = TRUE
              AND unaccent(c.name) ILIKE unaccent(:query) || '%'
            ORDER BY c.name
            LIMIT 10
            """)
    List<CitySearchResult> searchCities(String query);

}