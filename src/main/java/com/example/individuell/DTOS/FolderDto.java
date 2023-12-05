package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
public class FolderDto {
    @Id
    private String id;
    private String folderName;
    private String folderOwner;
    private List<FileInFolderDto> files;

    public FolderDto(String id, String folderName, String folderOwner, List<FileInFolderDto> files) {
        this.id = id;
        this.folderName = folderName;
        this.folderOwner = folderOwner;
        this.files = files;
    }
}
