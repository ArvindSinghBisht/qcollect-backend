package com.example.qcollect.mobile.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.mobile.dto.QueueRequest;
import com.example.qcollect.mobile.dto.QueueResponse;
import com.example.qcollect.mobile.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/queue")
public class QueueController {

    private final QueueService queueService;

    @PostMapping
    public ApiResponse<QueueResponse> addToQueue(

            @Valid
            @RequestBody QueueRequest request,

            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<QueueResponse>builder()

                .success(true)

                .message("Item added to sync queue.")

                .data(
                        queueService.addToQueue(
                                request,
                                loggedInUserId
                        )
                )

                .build();
    }

    @GetMapping
    public ApiResponse<List<QueueResponse>> getMyQueue(

            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<List<QueueResponse>>builder()

                .success(true)

                .message("Queue fetched successfully.")

                .data(
                        queueService.getUserQueue(
                                loggedInUserId
                        )
                )

                .build();
    }

    @GetMapping("/pending")
    public ApiResponse<List<QueueResponse>> getPendingQueue() {

        return ApiResponse.<List<QueueResponse>>builder()

                .success(true)

                .message("Pending queue fetched.")

                .data(
                        queueService.getPendingQueue()
                )

                .build();
    }

}