package com.example.testservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    // Removed duplicate @Id String id; it now inherits UUID from BaseEntity
    
    private String name;
    private String description;

    @Convert(converter = com.example.testservice.entity.converter.ListStringJsonConverter.class)
    @Column(name = "required_languages", columnDefinition = "json")
    private java.util.List<String> requiredLanguages;
}