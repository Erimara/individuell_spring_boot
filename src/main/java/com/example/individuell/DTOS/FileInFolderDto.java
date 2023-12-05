package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
@Getter
@Setter
public class FileInFolderDto implements Serializable {


    @Id
    private String id;
    private String fileOwner;

    public FileInFolderDto(String id, String fileOwner) {
        this.id = id;
        this.fileOwner = fileOwner;
    }
}
