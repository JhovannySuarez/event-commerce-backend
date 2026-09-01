package com.eventcommerce.eventtype.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("event_types")
public class EventType {

    @Id
    private UUID id;

    private UUID tenantId;

    private String name;

    private String description;

    private String color;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}