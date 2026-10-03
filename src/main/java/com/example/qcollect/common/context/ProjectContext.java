package com.example.qcollect.common.context;

import java.util.UUID;

public final class ProjectContext {

    private static final ThreadLocal<UUID> CURRENT_PROJECT = new ThreadLocal<>();

    private ProjectContext() {
    }

    public static void setProjectId(UUID projectId) {
        CURRENT_PROJECT.set(projectId);
    }

    public static UUID getProjectId() {
        return CURRENT_PROJECT.get();
    }

    public static void clear() {
        CURRENT_PROJECT.remove();
    }
}
