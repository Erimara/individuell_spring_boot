package com.example.individuell.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {


    @GetMapping("/set-session")
    public ResponseEntity<String> setSessionAttribute(HttpSession session){
        session.setAttribute("user", "example");
        System.out.println(session);
        System.out.println("created: " + session.getCreationTime());
        System.out.println("expiraton time: " + session.getLastAccessedTime());
        return ResponseEntity.ok("Session attribute is set");
    }


    @GetMapping("/get-session")
    public ResponseEntity<String> getSessionAttribute(HttpSession session){
        Object user = session.getAttribute("user");
        return ResponseEntity.ok("Session attribute: " + user.toString());
    }

    @GetMapping("/session-expired")
    String expiredSession(){
        return "session expired";
    }
}
