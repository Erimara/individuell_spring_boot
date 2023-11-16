package com.example.individuell.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Setter
@Getter
@Document(collection = "folders")
public class Folder {
    @Id
    private String id;

    private String folderName;

    @DBRef
    private List<File> myFiles;

    public Folder() {}
}
