package com.example.individuell.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/secured-login")
    String hello(){
        return "hello secured login";
    }
}
