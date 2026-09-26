package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // =====================================================
    // SIGNUP
    // =====================================================

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody User user) {

        try {

            User savedUser =
                    userService.registerUser(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User loginRequest) {

        try {

            User user =
                    userService.loginUser(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    );

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ADMIN - USER COUNT
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/count")
    public ResponseEntity<Long> getUserCount() {

        return ResponseEntity.ok(
                userService.getUserCount()
        );
    }


    // =====================================================
    // ADMIN - GET ALL USERS
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }


    // =====================================================
    // ADMIN - GET USER BY ID
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);

            if (!canManageUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You do not have permission to view this user"
                );
            }

            return ResponseEntity.ok(targetUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ADMIN - UPDATE USER
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            // -------------------------------------------------
            // PERMISSION CHECK
            // -------------------------------------------------

            if (!canManageUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You do not have permission to update this user"
                );
            }


            // -------------------------------------------------
            // GET REQUEST VALUES
            // -------------------------------------------------

            String name =
                    request.get("name");

            String email =
                    request.get("email");

            String phone =
                    request.get("phone");


            // -------------------------------------------------
            // UPDATE
            // -------------------------------------------------

            User updatedUser =
                    userService.updateUser(
                            id,
                            name,
                            email,
                            phone
                    );


            return ResponseEntity.ok(
                    updatedUser
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // SUPER ADMIN - CHANGE USER ROLE
    //
    // USER
    // ADMIN
    // SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/admin/{id}/role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            // -------------------------------------------------
            // CURRENT ADMIN
            // -------------------------------------------------

            String currentEmail =
                    authentication.getName();


            // -------------------------------------------------
            // PREVENT SELF ROLE CHANGE
            // -------------------------------------------------

            if (targetUser.getEmail()
                    .equalsIgnoreCase(currentEmail)) {

                return forbidden(
                        "You cannot change your own role"
                );
            }


            // -------------------------------------------------
            // ROLE VALUE
            // -------------------------------------------------

            String roleValue =
                    request.get("role");


            if (roleValue == null ||
                    roleValue.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success", false,
                                        "message", "Role is required"
                                )
                        );
            }


            User.Role newRole;

            try {

                newRole =
                        User.Role.valueOf(
                                roleValue
                                        .trim()
                                        .toUpperCase()
                        );

            } catch (IllegalArgumentException e) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success", false,
                                        "message",
                                        "Invalid role. Allowed roles: USER, ADMIN, SUPER_ADMIN"
                                )
                        );
            }


            // -------------------------------------------------
            // PREVENT DEMOTING / MANAGING SUPER ADMIN
            // -------------------------------------------------
            //
            // This prevents one SUPER_ADMIN from accidentally
            // changing another SUPER_ADMIN through the normal
            // user-management endpoint.
            //
            // -------------------------------------------------

            if (targetUser.getRole() == User.Role.SUPER_ADMIN) {

                return forbidden(
                        "SUPER_ADMIN accounts cannot be changed here"
                );
            }


            // -------------------------------------------------
            // CHANGE ROLE
            // -------------------------------------------------

            User updatedUser =
                    userService.changeUserRole(
                            id,
                            newRole
                    );


            return ResponseEntity.ok(
                    updatedUser
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ADMIN - BLOCK USER
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/admin/{id}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            if (!canManageUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You do not have permission to block this user"
                );
            }


            // -------------------------------------------------
            // PREVENT SELF BLOCK
            // -------------------------------------------------

            if (isCurrentUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You cannot block your own account"
                );
            }


            User user =
                    userService.blockUser(id);


            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ADMIN - UNBLOCK USER
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/admin/{id}/unblock")
    public ResponseEntity<?> unblockUser(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            if (!canManageUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You do not have permission to unblock this user"
                );
            }


            User user =
                    userService.unblockUser(id);


            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ADMIN - SET USER STATUS
    //
    // ADMIN + SUPER_ADMIN
    //
    // active = true
    // active = false
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> setUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            if (!canManageUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You do not have permission to change this user's status"
                );
            }


            // -------------------------------------------------
            // PREVENT SELF STATUS CHANGE
            // -------------------------------------------------

            if (isCurrentUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You cannot change your own account status"
                );
            }


            Boolean active =
                    request.get("active");


            if (active == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success", false,
                                        "message",
                                        "Active status is required"
                                )
                        );
            }


            User user =
                    userService.setUserStatus(
                            id,
                            active
                    );


            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ADMIN - FORCE LOGOUT
    //
    // ADMIN + SUPER_ADMIN
    //
    // This invalidates all existing JWT sessions for the
    // selected user.
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/admin/{id}/force-logout")
    public ResponseEntity<?> forceLogout(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            // -------------------------------------------------
            // PERMISSION CHECK
            // -------------------------------------------------

            if (!canManageUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You do not have permission to force logout this user"
                );
            }


            // -------------------------------------------------
            // PREVENT SELF FORCE LOGOUT
            // -------------------------------------------------

            if (isCurrentUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You cannot force logout your own account"
                );
            }


            // -------------------------------------------------
            // FORCE LOGOUT
            // -------------------------------------------------

            userService.forceLogout(id);


            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "All active sessions for the user have been logged out"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // SUPER ADMIN - DELETE USER
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User targetUser =
                    userService.getUserById(id);


            // -------------------------------------------------
            // PREVENT SELF DELETE
            // -------------------------------------------------

            if (isCurrentUser(
                    authentication,
                    targetUser)) {

                return forbidden(
                        "You cannot delete your own account"
                );
            }


            // -------------------------------------------------
            // PREVENT DELETE SUPER ADMIN
            // -------------------------------------------------

            if (targetUser.getRole()
                    == User.Role.SUPER_ADMIN) {

                return forbidden(
                        "SUPER_ADMIN accounts cannot be deleted here"
                );
            }


            // -------------------------------------------------
            // DELETE
            // -------------------------------------------------

            userService.deleteUser(id);


            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "User deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // CHECK WHETHER CURRENT ADMIN CAN MANAGE TARGET
    // =====================================================

    private boolean canManageUser(
            Authentication authentication,
            User targetUser) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return false;
        }


        // -------------------------------------------------
        // SUPER ADMIN
        // -------------------------------------------------

        if (hasRole(
                authentication,
                "ROLE_SUPER_ADMIN")) {

            return targetUser.getRole()
                    != User.Role.SUPER_ADMIN;
        }


        // -------------------------------------------------
        // ADMIN
        // -------------------------------------------------

        if (hasRole(
                authentication,
                "ROLE_ADMIN")) {

            return targetUser.getRole()
                    == User.Role.USER;
        }


        // -------------------------------------------------
        // NORMAL USER
        // -------------------------------------------------

        return false;
    }


    // =====================================================
    // CHECK CURRENT USER
    // =====================================================

    private boolean isCurrentUser(
            Authentication authentication,
            User targetUser) {

        if (authentication == null ||
                targetUser == null) {

            return false;
        }


        return targetUser.getEmail()
                .equalsIgnoreCase(
                        authentication.getName()
                );
    }


    // =====================================================
    // CHECK ROLE
    // =====================================================

    private boolean hasRole(
            Authentication authentication,
            String role) {

        if (authentication == null) {

            return false;
        }


        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                authority
                                        .getAuthority()
                                        .equals(role)
                );
    }


    // =====================================================
    // FORBIDDEN RESPONSE
    // =====================================================

    private ResponseEntity<Map<String, Object>> forbidden(
            String message) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        Map.of(
                                "success", false,
                                "message", message
                        )
                );
    }
}