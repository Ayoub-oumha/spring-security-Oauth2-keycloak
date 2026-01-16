package org.tricol.supplierchain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public endpoint - no authentication required");
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userInfo(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            "username", jwt.getClaim("preferred_username"),
            "email", jwt.getClaim("email"),
            "roles", jwt.getClaim("realm_access")
        ));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Admin endpoint - ADMIN role required");
    }

    @GetMapping("/responsable")
    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    public ResponseEntity<String> responsableEndpoint() {
        return ResponseEntity.ok("Responsable Achats endpoint");
    }

    @GetMapping("/magasinier")
    @PreAuthorize("hasRole('MAGASINIER')")
    public ResponseEntity<String> magasinierEndpoint() {
        return ResponseEntity.ok("Magasinier endpoint");
    }

    @GetMapping("/chef")
    @PreAuthorize("hasRole('CHEF_ATELIER')")
    public ResponseEntity<String> chefEndpoint() {
        return ResponseEntity.ok("Chef Atelier endpoint");
    }
}
