package com.example.qcollect.qcdelegation.service;

import com.example.qcollect.qcdelegation.dto.CreateQcDelegationRequest;
import com.example.qcollect.qcdelegation.dto.QcDelegationResponse;

import java.util.List;
import java.util.UUID;

public interface QcDelegationService {

    QcDelegationResponse createDelegation(
            UUID projectId,
            CreateQcDelegationRequest request,
            UUID loggedInUserId
    );

    List<QcDelegationResponse> getDelegations(
            UUID projectId,
            UUID loggedInUserId
    );

    void removeDelegation(
            UUID delegationId,
            UUID loggedInUserId
    );
}