package com.example.qcollect.report.specification;

import com.example.qcollect.report.dto.ReportFilterRequest;
import com.example.qcollect.submission.entity.Submission;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubmissionSpecification {

    public static Specification<Submission> filter(
            ReportFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (request.getProjectId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("form")
                                        .get("project")
                                        .get("id"),
                                request.getProjectId()));
            }

            if (request.getFormId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("form")
                                        .get("id"),
                                request.getFormId()));
            }

            if (request.getAccessorId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("accessor")
                                        .get("id"),
                                request.getAccessorId()));
            }

            if (request.getQualityCheckerId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("qualityChecker")
                                        .get("id"),
                                request.getQualityCheckerId()));
            }

            if (request.getStatus() != null) {

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                request.getStatus()));
            }

            if (request.getFromDate() != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("submittedAt"),
                                request.getFromDate()
                                        .atStartOfDay()));
            }

            if (request.getToDate() != null) {

                LocalDateTime to =
                        request.getToDate()
                                .atTime(23,59,59);

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("submittedAt"),
                                to));
            }

            return cb.and(
                    predicates.toArray(new Predicate[0]));
        };
    }

}