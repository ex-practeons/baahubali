package com.example.testservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "mock_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MockTest extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private TestSeries series;

    @Convert(converter = com.example.testservice.entity.converter.MapStringJsonConverter.class)
    @Column(name = "title_translations", columnDefinition = "json", nullable = false)
    private java.util.Map<String, String> titleTranslations;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(name = "is_section_order_strict")
    private Boolean isSectionOrderStrict;

    @Column(name = "shuffle_sections")
    private Boolean shuffleSections;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
