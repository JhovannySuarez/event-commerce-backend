package com.eventcommerce.venue.dto;

import java.time.LocalDate;
import java.util.List;

public record AvailableDate(
        LocalDate date,
        List<AvailableSpace> availableSpaces
) {
}