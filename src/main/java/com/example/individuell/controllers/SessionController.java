package com.example.individuell.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {


    @GetMapping("/set-session")
    public ResponseEntity<String> setSessionAttribute(){
        /*session.setAttribute("user", "example");*/
      /*  System.out.println("session created time: " + session.getCreationTime());
        System.out.println("session expiraton time: " + session.getLastAccessedTime());*/

        return ResponseEntity.ok("Session attribute is set");
    }


    @GetMapping("/get-session")
    public ResponseEntity<String> getSessionAttribute(){
        /*Object user = session.getAttribute("user");*/

        return ResponseEntity.ok("Session attribute");
    }

    @GetMapping("/session-expired")
    String expiredSession(HttpSession session)
    {
        session.invalidate();
        return "session expired";
    }
}
