package com.example.individuell.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents the @Document that resides in MongoDb. Lombok is used to get and set the fields in the class.
 * @DBRef connects the folder to a list of files in the database
 */
@Setter
@Getter
@Document(collection = "folders")
public class Folder {
    @Id
    private String id;
    private String folderName;
    @DBRef
    private User folderOwner;
    @DBRef
    private List<File> myFiles;
    public Folder() {}
    public List<File> getMyFiles() {
        if (myFiles == null) {
            myFiles = new ArrayList<>();
        }
        return myFiles;
    }
}
