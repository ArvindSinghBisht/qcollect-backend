# Multi-Project RBAC Integration

## What this solves

A single user account can now belong to multiple projects with a different role in each project.

Example:

- Project A -> `PROJECT_ADMIN`
- Project B -> `ACCESSOR`
- Project C -> `QUALITY_CHECKER`

The platform-level role in `users.system_role_id` remains separate from the project-level role in `project_users.role_id`.

## Source of truth

- Platform role: `users.system_role_id`
- Project role: `project_users` using `(project_id, user_id, role_id)`
- The existing unique constraint on `(project_id, user_id)` ensures one role per user inside one project.
- The same user can still have another role in another project because the project ID is different.

No database migration is required.

## Login response

`POST /auth/login` now includes active project memberships:

```json
{
  "success": true,
  "message": "Login Successful",
  "data": {
    "accessToken": "...",
    "tokenType": "Bearer",
    "userId": "...",
    "tenantId": "...",
    "systemRole": "USER",
    "firstName": "Amit",
    "lastName": "Kumar",
    "email": "amit@example.com",
    "projects": [
      {
        "projectId": "11111111-1111-1111-1111-111111111111",
        "projectName": "Project A",
        "role": "PROJECT_ADMIN"
      },
      {
        "projectId": "22222222-2222-2222-2222-222222222222",
        "projectName": "Project B",
        "role": "ACCESSOR"
      }
    ]
  }
}
```

The JWT is intentionally not changed. It identifies the user and tenant; it does not permanently store a selected project role.

## Refresh the user's projects

Use:

```http
GET /projects/my-projects
Authorization: Bearer <token>
```

Response items contain:

```json
{
  "projectId": "...",
  "projectName": "...",
  "roleId": "...",
  "roleName": "QUALITY_CHECKER"
}
```

Only active memberships, projects, and roles are returned.

## Assign an existing user to a project

Use:

```http
POST /projects/{projectId}/members
Authorization: Bearer <token>
Content-Type: application/json
X-Project-Id: {projectId}
```

```json
{
  "userId": "existing-user-id",
  "role": "QUALITY_CHECKER"
}
```

Allowed project roles are limited to:

- `PROJECT_ADMIN`
- `ACCESSOR`
- `QUALITY_CHECKER`

A tenant admin can assign a project admin. A project admin can assign accessors and quality checkers in that project. Tenant admins can also manage members in projects belonging to their tenant.

## Frontend flow

1. Log in once.
2. Read `data.projects` from the login response.
3. If the user has more than one project, show a project-selection screen.
4. Store the selected `projectId` and the returned role for UI routing/display.
5. Send `X-Project-Id` on project-scoped requests.
6. Call `GET /projects/my-projects` when memberships need to be refreshed.

The frontend role is only for navigation and UI visibility. The backend always validates the logged-in user against `project_users` for the requested project.

## Security changes included

- Active membership is required for project authorization.
- The generic member-management endpoints now receive the authenticated user's ID.
- Only tenant admins can assign or remove project admins.
- Project admins can manage accessors and quality checkers only in projects where they are active project admins.
- Inactive memberships, projects, and roles are excluded from login and `my-projects` responses.
- `X-Project-Id` is allowed by CORS.
- Invalid `X-Project-Id` values return HTTP 400.

## New files

- `common/context/ProjectContext.java`
- `common/context/ProjectContextFilter.java`
- `project/dto/ProjectMembershipResponse.java`
- `project/dto/MyProjectResponse.java`
- `project/repository/ProjectMembershipProjection.java`

## Modified files

- `auth/dto/LoginResponse.java`
- `auth/service/AuthServiceImpl.java`
- `config/SecurityConfig.java`
- `project/controller/ProjectController.java`
- `project/dto/AddProjectMemberRequest.java`
- `project/repository/ProjectUserRepository.java`
- `project/service/ProjectAuthorizationService.java`
- `project/service/ProjectAuthorizationServiceImpl.java`
- `project/service/ProjectService.java`
- `project/service/ProjectServiceImpl.java`

## Local validation

Run from the project root:

```bash
./mvnw clean test
```

Then verify these scenarios:

1. One user is assigned as project admin in Project A.
2. The same user is assigned as accessor in Project B.
3. The same user is assigned as quality checker in Project C.
4. Login returns all three projects with the correct role for each.
5. The user can perform only the role-authorized operations inside each selected project.
6. Sending another project's ID does not grant access unless a matching active membership exists.
