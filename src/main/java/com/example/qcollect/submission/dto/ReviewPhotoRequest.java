package com.example.qcollect.submission.dto;

import com.example.qcollect.submission.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPhotoRequest {

    @NotNull(message = "Review status is required.")
    private ReviewStatus status;

    @Size(
            max = 500,
            message = "Remarks cannot exceed 500 characters."
    )
    private String remarks;
}