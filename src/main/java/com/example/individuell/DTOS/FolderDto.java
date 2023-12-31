package com.example.individuell.DTOS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.List;

/**
 * Data transfer object which is used to only show relevant data in responses and for not exposing critical data
 */
@Getter
@Setter
public class FolderDto implements Serializable {
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
