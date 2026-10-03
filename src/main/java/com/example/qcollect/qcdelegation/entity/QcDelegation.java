package com.example.qcollect.qcdelegation.entity;



import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "qc_delegations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QcDelegation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    /*
     * Assessor whose submissions are affected
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessor_id", nullable = false)
    private User accessor;

    /*
     * Original assigned QC
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_qc_id", nullable = false)
    private User originalQc;

    /*
     * Temporary replacement QC
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_qc_id", nullable = false)
    private User delegateQc;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String reason;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}