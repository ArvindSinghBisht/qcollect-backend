package com.example.qcollect.common.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ProjectContextFilter extends OncePerRequestFilter {

    public static final String PROJECT_HEADER = "X-Project-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String projectIdHeader = request.getHeader(PROJECT_HEADER);

            if (StringUtils.hasText(projectIdHeader)) {
                try {
                    ProjectContext.setProjectId(UUID.fromString(projectIdHeader));
                } catch (IllegalArgumentException ex) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid X-Project-Id header."
                    );
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            ProjectContext.clear();
        }
    }
}
