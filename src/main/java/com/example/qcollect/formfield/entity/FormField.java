package com.example.qcollect.formfield.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.formfield.enums.FieldType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "form_fields")
public class FormField extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    @Column(nullable = false)
    private Boolean required;

    private String placeholder;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "field_order")
    private Integer fieldOrder;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "validation_json", columnDefinition = "TEXT")
    private String validationJson;

}