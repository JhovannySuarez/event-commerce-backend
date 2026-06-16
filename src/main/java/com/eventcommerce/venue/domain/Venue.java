package com.eventcommerce.venue.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("venues")
public class Venue {

    @Id
    private UUID id;

    /**
     * Parent tenant.
     */
    private UUID tenantId;
    private String name;

    /**
     * Public URL identifier.
     */
    private String slug;

    /**
     * Public description.
     */
    private String description;

    /**
     * Physical address.
     */
    private String address;
    private String city;
    private String country;
    private String phone;
    private String email;

    /**
     * Coordinates used for maps.
     */
    private Double latitude;
    private Double longitude;

    /**
     * Social networks and website.
     */
    private String instagramUrl;

    private String facebookUrl;

    private String websiteUrl;

    /**
     * Display order inside the tenant.
     */
    private Integer displayOrder;

    /**
     * Featured venue.
     */
    private boolean highlighted;

    /**
     * Soft delete flag.
     */
    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}