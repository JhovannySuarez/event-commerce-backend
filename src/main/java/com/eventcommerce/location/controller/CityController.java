package com.eventcommerce.location.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventcommerce.location.dto.CitySearchResponse;
import com.eventcommerce.location.service.CityService;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<CitySearchResponse> searchCities(
            @RequestParam String query
    ) {
        return cityService.searchCities(query);
    }
}