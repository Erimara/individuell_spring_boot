package com.example.individuell.Assemblers;


import com.example.individuell.controllers.FileController;
import com.example.individuell.controllers.FolderController;
import com.example.individuell.controllers.UserController;
import com.example.individuell.models.File;
import com.example.individuell.models.User;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FileModelAssembler implements RepresentationModelAssembler<File, EntityModel<File>>{
    @Override
    public EntityModel<File> toModel(File file) {
        return EntityModel.of(file,
                linkTo(methodOn(FolderController.class).getFolderById(file.getId())).withSelfRel(),
                linkTo(methodOn(FolderController.class).getAllFolders()).withRel("files"));
    }
}

