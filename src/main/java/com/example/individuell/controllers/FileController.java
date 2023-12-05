package com.example.individuell.controllers;

import com.example.individuell.Assemblers.FileDtoModelAssembler;
import com.example.individuell.Assemblers.FileModelAssembler;
import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.models.File;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * This controller handles the requests between the client and the server
 */
@RestController
public class FileController {

    FileService fileService;

    FileDtoModelAssembler fileDtoModelAssembler;
    FileModelAssembler fileModelAssembler;

    FileRepository fileRepository;

    UserRepository userRepository;
    @Autowired
    public FileController(FileService fileService, FileDtoModelAssembler fileDtoModelAssembler, FileModelAssembler fileModelAssembler, FileRepository fileRepository, UserRepository userRepository) {
        this.fileService = fileService;
        this.fileDtoModelAssembler = fileDtoModelAssembler;
        this.fileModelAssembler = fileModelAssembler;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    /**
     * Post method to upload a single file, without a connection to a folder
     *
     * @param file is a Multipart file, which is a class in spring boot
     * @return ResponseEntity<file>
     * @throws IOException exception is needed to be able to handle errors when uploading
     */
    @PostMapping("/upload-file")
    public ResponseEntity<File> uploadSingleFile(@RequestParam("file") MultipartFile file) throws IOException {
        EntityModel<File> entityModel = fileModelAssembler.toModel(fileService.uploadSingleFile(file));
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }

    /**
     * Post method to upload a file to a specific folder
     *
     * @param file is a Multipart file, which is a class in spring boot
     * @param id gets the id of a specific folder
     * @return ResponseEntity<file>
     * @throws IOException exception is needed to be able to handle errors when uploading
     */
    @PostMapping("/folder/upload-file/{id}")
    public ResponseEntity<File> uploadFileToFolder(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        EntityModel<File> entityModel = fileService.uploadFileToFolder(file,id);
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }

    /**
     * Getter method to fetch all the files from the database that are connected to the user
     * @return CollectionModel<EntityModel < FileDto>>>
     */
    @GetMapping("/my-files")
    public CollectionModel<EntityModel<FileDto>> viewMyFiles() {
        String user = userRepository.getLoggedInUser().getName();
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
     * @return CollectionModel<EntityModel < FileDto>>>
     */
    @GetMapping("/files")
    public CollectionModel<EntityModel<File>> getAllFiles() {
        var files = fileService.getAllFiles().stream()
                .map(fileModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(files);
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
    public ResponseEntity<File> deleteFileById(@PathVariable String id) {
        fileService.deleteFileById(id);
        return ResponseEntity.noContent().build();
    }
}
