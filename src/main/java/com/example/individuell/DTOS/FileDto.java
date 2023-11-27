package com.example.individuell.DTOS;

import com.example.individuell.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashMap;

@Getter
@Setter
public class FileDto {


    @Id
    private String id;
    private HashMap<String, String> fileProperties;
    private String fileOwner;

    public FileDto(String id, HashMap<String, String> fileProperties, String fileOwner) {
        this.id = id;
        this.fileProperties = fileProperties;
        this.fileOwner = fileOwner;
    }
}
