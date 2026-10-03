package com.example.qcollect.submission.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.submission.enums.PhotoUploadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "submission_photos")
public class SubmissionPhoto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 1000)
    private String filePath;

    private Double latitude;

    private Double longitude;

    @OneToOne(mappedBy = "submissionPhoto",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private SubmissionPhotoReview review;

    @Column(name = "mobile_photo_id", unique = true)
    private UUID mobilePhotoId;
    @Column(name = "photo_url", length = 1000)
    private String photoUrl;
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

    @Column(name = "upload_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PhotoUploadStatus uploadStatus = PhotoUploadStatus.PENDING;
}