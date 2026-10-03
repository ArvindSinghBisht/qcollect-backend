package com.example.qcollect.qcdelegation.dto;



import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QcDelegationResponse {

    private UUID id;

    private UUID projectId;

    private UUID formId;

    private String formName;

    private UUID accessorId;

    private String accessorName;

    private UUID originalQcId;

    private String originalQcName;

    private UUID delegateQcId;

    private String delegateQcName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    private Boolean active;
}