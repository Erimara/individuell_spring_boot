package com.example.individuell.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {


    @GetMapping("/login-successful")
    public String loginSuccess(){
        return """
                Login successful.
                
                Welcome!
                """;
    }
    @GetMapping("/start-page")
    public String startPage(){
        return "Welcome to the startpage, please log in to proceed";
    }

}
