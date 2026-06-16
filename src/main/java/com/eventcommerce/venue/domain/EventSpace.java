package com.eventcommerce.venue.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("event_spaces")
public class EventSpace {

    @Id
    private UUID id;

    private UUID venueId;

    private String name;

    private String description;

    private Integer capacityMin;

    private Integer capacityMax;

    private BigDecimal basePrice;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}