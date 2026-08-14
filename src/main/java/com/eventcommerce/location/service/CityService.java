package com.eventcommerce.location.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.eventcommerce.location.dto.CitySearchResponse;
import com.eventcommerce.location.repository.CityRepository;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CitySearchResponse> searchCities(String query) {

        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        return cityRepository.searchCities(query.trim())
                .stream()
                .map(city -> new CitySearchResponse(
                        city.id(),
                        city.name(),
                        city.state() + ", " + city.country()
                ))
                .toList();
    }
}