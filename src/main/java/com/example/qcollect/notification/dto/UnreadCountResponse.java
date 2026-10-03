package com.example.qcollect.notification.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountResponse {

    private long count;

}