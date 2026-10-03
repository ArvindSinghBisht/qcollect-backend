package com.example.qcollect.formversion.service;

import com.example.qcollect.formversion.dto.VersionDetailsResponse;
import com.example.qcollect.formversion.dto.VersionResponse;

import java.util.List;
import java.util.UUID;

public interface FormVersionService {

    /**
     * List all versions of a form.
     */
    List<VersionResponse> getVersions(
            UUID formId
    );

    /**
     * Get a specific version.
     */
    VersionDetailsResponse getVersion(
            UUID formId,
            Integer version
    );

    /**
     * Publish (rollback to) a specific version.
     */
    VersionDetailsResponse publishVersion(
            UUID formId,
            Integer version,
            UUID loggedInUserId
    );

}