package com.example.individuell.models;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

/**
 * Class which represents the @Document that resides in MongoDb. Lombok is used to get and set the fields in the class.
 * @DBRef connects the file to a user in the database
 */
@Setter
@Getter
@Document(collection = "files")
public class File {
    @Id
    private String id;
    private String name;
    private HashMap<String, String> fileProperties;
    @DBRef
    private User fileOwner;
    public File() {}
}
