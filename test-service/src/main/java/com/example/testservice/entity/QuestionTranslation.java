package com.example.testservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "question_translations")
@Getter
@Setter
public class QuestionTranslation extends BaseEntity {
    // Removed redundant @Id private String id; since it inherits UUID from BaseEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String language;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "options_json", columnDefinition = "json")
    private String optionsJson;
}