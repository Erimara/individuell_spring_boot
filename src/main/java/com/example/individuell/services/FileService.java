package com.example.individuell.services;

import com.example.individuell.Assemblers.FileModelAssembler;
import com.example.individuell.models.File;
import com.example.individuell.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    FileRepository fileRepository;

    FileModelAssembler assembler;

    @Autowired
    public FileService(FileRepository fileRepository, FileModelAssembler assembler) {
        this.fileRepository = fileRepository;
        this.assembler = assembler;
    }
    public ResponseEntity<File> handleFileUpload(MultipartFile file) throws IOException {
        HashMap<String, String> fileMap = new HashMap<>();
        File savedFile = new File();
        fileMap.put("Filename: ", file.getOriginalFilename());
        fileMap.put("Bytes: ", file.getBytes().toString());
        fileMap.put("File type: ", file.getContentType());

        savedFile.setFileProperties(fileMap);
        fileRepository.save(savedFile);
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
    public ResponseEntity<?> getFileById(String id){
        return ResponseEntity.ok(fileRepository.findById(id));
    }

    public ResponseEntity<File> deleteFileById(String id){
        fileRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}