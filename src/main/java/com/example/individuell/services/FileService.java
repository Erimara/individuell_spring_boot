package com.example.individuell.services;

import com.example.individuell.Assemblers.FileModelAssembler;
import com.example.individuell.models.File;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.repositories.FolderRepository;
import com.example.individuell.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    FileRepository fileRepository;
    UserRepository userRepository;
    FileModelAssembler assembler;

    FolderRepository folderRepository;
    @Autowired
    public FileService(FileRepository fileRepository,
                       UserRepository userRepository,
                       FileModelAssembler assembler,
                       FolderRepository folderRepository)
    {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.folderRepository = folderRepository;
    }

    public ResponseEntity<File> handleFileUpload(MultipartFile file) throws IOException {
        HashMap<String, String> fileMap = new HashMap<>();
        String getUsername = fileRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(getUsername);
        File savedFile = new File();
        fileMap.put("Filename: ", file.getOriginalFilename());
        fileMap.put("Bytes: ", file.getBytes().toString());
        fileMap.put("File type: ", file.getContentType());
        savedFile.setFileOwner(user);
        savedFile.setFileProperties(fileMap);
        fileRepository.save(savedFile);
        EntityModel<File> entityModel = assembler.toModel(savedFile);
        //TODO: Double check the hyperlinks to the fileOwner
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }
    public ResponseEntity<File> uploadFileToFolder(MultipartFile file, String id) throws IOException {
        HashMap<String, String> fileMap = new HashMap<>();
        String getUsername = fileRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(getUsername);
        Folder folder = folderRepository.findById(id).orElseThrow(RuntimeException::new);

        File savedFile = new File();
        fileMap.put("Filename: ", file.getOriginalFilename());
        fileMap.put("Bytes: ", file.getBytes().toString());
        fileMap.put("File type: ", file.getContentType());

        savedFile.setFileOwner(user);
        savedFile.setFileProperties(fileMap);
        folder.getMyFiles().add(savedFile);

        fileRepository.save(savedFile);
        folderRepository.save(folder);
        EntityModel<File> entityModel = assembler.toModel(savedFile);

        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }

    public CollectionModel<EntityModel<File>> getAllFiles(){
        List<EntityModel<File>> files = fileRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(files);
    }
    public CollectionModel<EntityModel<File>> viewMyFiles(){
        String username = fileRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(username);
        List<EntityModel<File>> files = fileRepository.findAll()
                .stream()
                .filter(x -> Objects.equals(x.getFileOwner().getId(), user.getId()))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(files);
    }
    public ResponseEntity<?> getFileById(String id){
        return ResponseEntity.ok(fileRepository.findById(id));
    }

    public ResponseEntity<File> deleteFileById(String id){
        fileRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}