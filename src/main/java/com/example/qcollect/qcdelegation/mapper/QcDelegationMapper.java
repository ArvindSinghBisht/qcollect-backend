package com.example.qcollect.qcdelegation.mapper;

import com.example.qcollect.qcdelegation.dto.QcDelegationResponse;
import com.example.qcollect.qcdelegation.entity.QcDelegation;
import org.springframework.stereotype.Component;

@Component
public class QcDelegationMapper {

    public QcDelegationResponse toResponse(QcDelegation delegation) {

        return QcDelegationResponse.builder()

                .id(delegation.getId())

                .projectId(delegation.getProject().getId())

                .formId(delegation.getForm().getId())

                .formName(delegation.getForm().getName())

                .accessorId(delegation.getAccessor().getId())

                .accessorName(
                        delegation.getAccessor().getFirstName()
                                + " "
                                + delegation.getAccessor().getLastName()
                )

                .originalQcId(
                        delegation.getOriginalQc().getId()
                )

                .originalQcName(
                        delegation.getOriginalQc().getFirstName()
                                + " "
                                + delegation.getOriginalQc().getLastName()
                )

                .delegateQcId(
                        delegation.getDelegateQc().getId()
                )

                .delegateQcName(
                        delegation.getDelegateQc().getFirstName()
                                + " "
                                + delegation.getDelegateQc().getLastName()
                )

                .startDate(
                        delegation.getStartDate()
                )

                .endDate(
                        delegation.getEndDate()
                )

                .reason(
                        delegation.getReason()
                )

                .active(
                        delegation.getActive()
                )

                .build();
    }
}