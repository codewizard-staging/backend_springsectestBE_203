package com.replicacia.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.replicacia.model.*;
import com.replicacia.repo.ApplicationRepository;
import com.replicacia.repo.PermissionRepository;
import com.replicacia.rest.admin.dto.PermissionRequestDTO;
import com.replicacia.rest.security.service.UserService;
import com.replicacia.service.PermissionService;
import com.replicacia.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class AppsDataLoader {

    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private PermissionService permissionService;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired protected ObjectMapper objectMapper;

    @EventListener
    public void appReady(final ApplicationReadyEvent event) {
        try {
            List<Application> apps = objectMapper.readValue(
                    new ClassPathResource("Apps.json").getInputStream(),
                    new TypeReference<List<Application>>() {});

            log.info("Apps: " + apps);
            for (Application app : apps) {
                log.info("Processing Application: " + app.getAppName());

                // Ensure user exists
                AppUser user = createUser(app.getAppUser());
                log.info("User Created/Found: " + user.getUsername());

                // Ensure role exists
                Role role = createRole(app.getRoles());
                log.info("Role Created/Found: " + role.getName());

                // Ensure permissions exist
                createPermissions(role, app.getRoles().getPermissions());

                // Assign role to user
                assignRole(user, role);

                // Ensure application is stored
                saveApplication(app, user, role);
            }
        } catch (IOException e) {
            log.error("Unable to load default data", e);
        }
    }

    private void saveApplication(Application app, AppUser user, Role role) {
        Optional<Application> existingApp = applicationRepository.findByAppName(app.getAppName());
        if (!existingApp.isPresent()) {
            app.setAppUser(user);
            app.setRoles(role);
            applicationRepository.save(app);
            log.info("Saved Application: " + app.getAppName());
        } else {
            log.info("Application already exists: " + app.getAppName());
        }
    }

    private void assignRole(final AppUser appUser, final Role role) {
        if (!appUser.getRoles().contains(role)) {
            appUser.getRoles().add(role);
            userService.update(appUser);
            log.info("Assigned Role: " + role.getName() + " to User: " + appUser.getUsername());
        }
    }

    private void createPermissions(final Role role, Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            log.warn("No permissions found for role: " + role.getName());
            return;
        }

        for (Permission permission : permissions) {
            Optional<Permission> existingPermission =
                    permissionRepository.findByApiAndApiAccess(permission.getApi(), permission.getApiAccess());

            existingPermission.orElseGet(() -> {
                PermissionRequestDTO dto = new PermissionRequestDTO();
                dto.setRoleId(role.getPublicId());
                dto.setApi(permission.getApi());
                dto.setApiAccess(permission.getApiAccess());
                dto.setName(permission.getName());
                Permission newPermission = permissionService.createPermission(dto);
                log.info("Created Permission: " + newPermission.getName() + " for Role: " + role.getName());
                return newPermission;
            });
        }
    }

    private Role createRole(Role roleData) {
        return roleService.findByName(roleData.getName())
                .orElseGet(() -> {
                    Role newRole = roleService.saveRole(roleData);
                    log.info("Created Role: " + newRole.getName());
                    return newRole;
                });
    }

    private AppUser createUser(AppUser userData) {
        AppUser existingUser = userService.findAllByUserName(userData.getUsername());
        if (Objects.isNull(existingUser)) {
            existingUser = userService.save(userData, false);
            userService.verifyUser(existingUser, null, false);
            log.info("Created User: " + existingUser.getUsername());
        } else {
            log.info("User already exists: " + existingUser.getUsername());
        }
        return existingUser;
    }
}
