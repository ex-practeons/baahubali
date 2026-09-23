package com.example.testservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_series")
@Getter
@Setter
@SQLRestriction("deleted_at IS NULL") // Auto-filters soft-deleted series
public class TestSeries extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted_at IS NULL")
    private List<MockTest> mockTests = new ArrayList<>();
}