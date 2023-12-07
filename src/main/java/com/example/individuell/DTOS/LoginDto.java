package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object which is used to only show relevant data in responses and for not exposing critical data
 */
@Getter
@Setter
public class LoginDto {
    private String email;
    private String password;
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
