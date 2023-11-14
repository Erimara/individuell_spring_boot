package com.example.individuell.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
@Setter
@Getter
@Document(collection = "users")
public class User {
    private String email;
    private String password;

    @DBRef
    Folder myFolders;
}
