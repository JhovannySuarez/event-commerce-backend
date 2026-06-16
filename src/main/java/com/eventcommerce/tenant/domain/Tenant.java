package com.eventcommerce.tenant.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("tenants")
public class Tenant {

    @Id
    private UUID id;

    private String name;
    private String slug;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}