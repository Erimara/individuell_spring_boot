package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Data transfer object which is used to only show relevant data in responses and for not exposing critical data
 */
@Getter
@Setter
public class FileInFolderDto implements Serializable {


    @Id
    private String id;
    private String fileName;

    public FileInFolderDto(String id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }
}
