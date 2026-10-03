package com.example.qcollect.sync.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncResponse {

    private Integer uploaded;

    private Integer downloaded;

    private String message;

    @Builder.Default
    private List<SyncConflictDto> conflicts = new ArrayList<>();

}