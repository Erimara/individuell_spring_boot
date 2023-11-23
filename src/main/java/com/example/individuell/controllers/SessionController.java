package com.example.individuell.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/session-expired")
    String expiredSession(HttpSession session)
    {
        session.invalidate();
        return "session expired";
    }
}
