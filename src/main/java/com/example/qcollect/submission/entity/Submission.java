package com.example.qcollect.submission.entity;



import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.formversion.entity.FormVersion;
import com.example.qcollect.submission.enums.SubmissionStatus;
import com.example.qcollect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import com.example.qcollect.submission.entity.SubmissionPhoto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessor_id", nullable = false)
    private User accessor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(length = 2000)
    private String remarks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne
    @JoinColumn(name="checked_by")
    private User qualityChecker;

    private LocalDateTime checkedAt;

    @Column(length = 500)
    private String remarksString;

    @Column(name = "answers_json", columnDefinition = "TEXT")
    private String answersJson;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "is_synced")
    @Builder.Default
    private Boolean synced = false;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @Column(name = "sync_version")
    @Builder.Default
    private Integer syncVersion = 1;

    @Column(name = "last_modified_device")
    private String lastModifiedDevice;
    @OneToMany(
            mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<SubmissionAnswer> answers = new ArrayList<>();
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_version_id", nullable = false)
    private FormVersion formVersion;
}