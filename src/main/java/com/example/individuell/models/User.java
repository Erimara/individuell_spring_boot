package com.example.individuell.models;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    @DBRef
    private List<Folder> myFolders;

    public User() {}
}
