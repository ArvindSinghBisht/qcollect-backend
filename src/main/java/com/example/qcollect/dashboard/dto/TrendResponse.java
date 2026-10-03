package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TrendResponse {

    private LocalDate date;

    private Long total;

}