package com.example.individuell.models;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Setter
@Getter
@Document(collection = "files")
public class File {
    @Id
    private String id;
    private HashMap<String, String> fileProperties;
    public File() {}
}
