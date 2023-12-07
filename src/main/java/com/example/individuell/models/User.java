package com.example.individuell.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * Class which represents the @Document that resides in MongoDb. Lombok is used to get and set the fields in the class.
 */
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
