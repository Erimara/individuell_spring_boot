package com.example.individuell.controllers;

import com.example.individuell.Assemblers.FileDtoModelAssembler;
import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.models.File;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;

/**
 * This controller handles the requests between the client and the server
 */
@RestController
public class FileController {

    FileService fileService;

    FileDtoModelAssembler fileDtoModelAssembler;

    FileRepository fileRepository;
    @Autowired
    public FileController(FileService fileService,FileDtoModelAssembler fileDtoModelAssembler,FileRepository fileRepository) {
        this.fileService = fileService;
        this.fileDtoModelAssembler = fileDtoModelAssembler;
        this.fileRepository = fileRepository;
    }

    /**
     * Post method to upload a single file, without a connection to a folder
     * @param file
     * @return ResponseEntity<file>
     * @throws IOException
     */
    @PostMapping("/upload-file")
    public ResponseEntity<File> uploadSingleFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.uploadSingleFile(file);
    }

    /**
     * Post method to upload a file to a specific folder
     * @param file
     * @param id
     * @return ResponseEntity<file>
     * @throws IOException
     */
    @PostMapping("/folder/upload-file/{id}")
    public ResponseEntity<File> uploadFileToFolder(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        return fileService.uploadFileToFolder(file, id);
    }

    /**
     * Getter method to fetch all the files from the database that are connected to the user
     * @return CollectionModel<EntityModel<FileDto>>>
     */
    @GetMapping("/my-files")
    public CollectionModel<EntityModel<FileDto>> viewMyFiles(){
        String user = fileRepository.getLoggedInUser().getName();
        var myFiles = fileService.viewMyFiles(user).stream().map((file) -> fileDtoModelAssembler.toModel(file))
                .collect(Collectors.toList());
        return CollectionModel.of(myFiles);
    }
    /**
     * Getter method to fetch a specific file from the database
     * @return ResponseEntity<File>
     */
    @GetMapping("/files/{id}")
    public ResponseEntity<File> getFileById(@PathVariable String id) throws FileNotFoundException {
        return ResponseEntity.ok(fileService.getFileById(id));
    }

    /**
     * Getter method to fetch all the files from the database
     * @return CollectionModel<EntityModel<FileDto>>>
     */
    @GetMapping("/files")
    public CollectionModel<EntityModel<File>> getAllFiles(){
        return fileService.getAllFiles();
    }
    /**
     * Method for downloading a file. The output is in bytes
     * @return ResponseEntity<ByteArrayResource>
     */
    @GetMapping("/files/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String id) throws FileNotFoundException, IOException {
        ByteArrayResource file = fileService.downloadFile(id);
        HttpHeaders header = new HttpHeaders();
        header.setContentDispositionFormData("file", file.getFilename());
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(header)
                .body(file);
    }
    /**
     * Method for deleting a file by ID. Returns a "No content, 204" on success.
     */
    @DeleteMapping("/my-files/{id}")
    public ResponseEntity<File> deleteFileById(@PathVariable String id){
        return fileService.deleteFileById(id);
    }

}
