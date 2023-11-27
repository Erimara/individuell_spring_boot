package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class FolderDto {
    @Id
    private String id;
    private String folderName;
    private String folderOwner;

    public FolderDto(String id, String folderName, String folderOwner) {
        this.id = id;
        this.folderName = folderName;
        this.folderOwner = folderOwner;
    }
}
