package com.example.qcollect.formversion.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.form.entity.Form;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "survey_json", columnDefinition = "TEXT", nullable = false)
    private String surveyJson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean published = false;
}