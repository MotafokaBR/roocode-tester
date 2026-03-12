package com.aliasforwarder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        // Redirects are handled by Spring Security's OAuth2 login flow.
        // This endpoint exists so the frontend knows where to point.
        return ResponseEntity.ok(Map.of(
                "loginUrl", "/oauth2/authorization/oidc-provider"
        ));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of(
                "sub", getStringAttribute(principal, "sub"),
                "name", getStringAttribute(principal, "name"),
                "email", getStringAttribute(principal, "email"),
                "authenticated", true
        ));
    }

    private String getStringAttribute(OAuth2User principal, String key) {
        Object value = principal.getAttribute(key);
        return value != null ? value.toString() : "";
    }
}
