package com.example.qcollect.submission.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.formfield.entity.FormField;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable =false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private FormField field;

    @Column(columnDefinition = "TEXT")
    private String value;
    @OneToOne(mappedBy = "submissionAnswer",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private SubmissionAnswerReview review;

    @Column(nullable = false)
    @Builder.Default
    private Boolean synced = false;

    private LocalDateTime syncedAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer syncVersion = 0;

    private String lastModifiedDevice;
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    private LocalDateTime deletedAt;
}