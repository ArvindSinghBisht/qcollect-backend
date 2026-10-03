
package com.example.qcollect.formassignment.dto;




import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {

    private UUID assignmentId;

    private UUID projectId;

    private UUID formId;

    private String formName;

    private UUID accessorId;

    private String accessorName;

    private UUID qualityCheckerId;

    private String qualityCheckerName;

    private UUID assignedBy;

    private String assignedByName;

    private LocalDateTime assignedAt;

    private Boolean active;

}