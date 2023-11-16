package com.example.individuell.services;

import com.example.individuell.Assemblers.FolderModelAssembler;
import com.example.individuell.models.Folder;
import com.example.individuell.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    FolderRepository folderRepository;
    FolderModelAssembler assembler;

    @Autowired
    public FolderService(FolderRepository folderRepository, FolderModelAssembler assembler) {
        this.folderRepository = folderRepository;
        this.assembler = assembler;
    }

    public ResponseEntity<Folder> createFolder(Folder folder) {
        EntityModel<Folder> entityModel = assembler.toModel(folderRepository.save(folder));
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }

    public CollectionModel<EntityModel<Folder>> getAllFolders(){
        List<EntityModel<Folder>> folders = folderRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(folders);
    }
    public ResponseEntity<?> getFolderById(String id){
        return ResponseEntity.ok(folderRepository.findById(id));
    }

    public ResponseEntity<Folder> deleteFolderById(String id){
        folderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
