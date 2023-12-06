package com.example.individuell.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller handles endpoints which users are redirected to depending on if they've logged in / are logged in
 */
@RestController
public class LoginController {

    /**
     * Redirect if a user logs in successfull
     *
     * @return String of text
     */
    @GetMapping("/login-successful")
    public String loginSuccess() {
        return """
                Login successful.
                                
                Welcome!
                """;
    }

    /**
     * Redirect if user is not logged in / if user logs out.
     *
     * @return Sting of text
     */
    @GetMapping("/start-page")
    public String startPage() {
        return "Welcome to the startpage, please log in to proceed";
    }
}
