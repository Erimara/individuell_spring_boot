package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Data transfer object which is used to only show relevant data in responses and for not exposing critical data
 */
@Getter
@Setter
public class FileDto implements Serializable {


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
