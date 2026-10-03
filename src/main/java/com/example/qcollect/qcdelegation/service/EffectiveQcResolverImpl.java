package com.example.qcollect.qcdelegation.service;

import com.example.qcollect.qcdelegation.entity.QcDelegation;
import com.example.qcollect.qcdelegation.repository.QcDelegationRepository;
import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EffectiveQcResolverImpl
        implements EffectiveQcResolver {

    private final QcDelegationRepository delegationRepository;

    @Override
    public User resolve(Submission submission) {

        UUID formId =
                submission.getForm().getId();

        UUID accessorId =
                submission.getAccessor().getId();

        LocalDate today = LocalDate.now();

        QcDelegation delegation =
                delegationRepository
                        .findByForm_IdAndAccessor_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                formId,
                                accessorId,
                                today,
                                today
                        )
                        .orElse(null);

        /*
         * Active Delegation Exists
         */
        if (delegation != null) {

            return delegation.getDelegateQc();

        }

        /*
         * No Delegation
         */
        return submission.getQualityChecker();
    }
}