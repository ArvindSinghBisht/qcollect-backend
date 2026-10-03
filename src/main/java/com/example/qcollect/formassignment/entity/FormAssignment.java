package com.example.qcollect.formassignment.entity;



import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "form_assignments",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "form_id",
                                "accessor_id"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "form_id",
            nullable = false
    )
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accessor_id",
            nullable = false
    )
    private User accessor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "quality_checker_id",
            nullable = false
    )
    private User qualityChecker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assigned_by",
            nullable = false
    )
    private User assignedBy;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}