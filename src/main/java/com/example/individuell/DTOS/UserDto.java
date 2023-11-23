package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
