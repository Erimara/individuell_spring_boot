package com.example.individuell.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String role;

    public User() {}
}
