package com.example.qcollect.submission.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAnswersRequest {

    @NotBlank(message = "Answers JSON is required.")
    private String answersJson;

}