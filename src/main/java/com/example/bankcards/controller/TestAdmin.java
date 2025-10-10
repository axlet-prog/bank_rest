package com.example.bankcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-09 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin")
public class TestAdmin {

    @PostMapping("/test")
    public ResponseEntity<String> login(
    ) {
        return ResponseEntity.ok("admin");
    }
}
