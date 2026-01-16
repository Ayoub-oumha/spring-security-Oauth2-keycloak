package org.tricol.supplierchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.service.KeycloakService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KeycloakAuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> tokens = keycloakService.login(request.username(), request.password());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody RefreshRequest request) {
        Map<String, Object> tokens = keycloakService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        keycloakService.logout(request.refreshToken());
        return ResponseEntity.ok().build();
    }

    record LoginRequest(String username, String password) {}
    record RefreshRequest(String refreshToken) {}
}
