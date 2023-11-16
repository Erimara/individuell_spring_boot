package com.example.individuell.Assemblers;

import com.example.individuell.controllers.FileController;
import com.example.individuell.controllers.FolderController;
import com.example.individuell.models.File;
import com.example.individuell.models.Folder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class FolderModelAssembler implements RepresentationModelAssembler<Folder, EntityModel<Folder>> {
    @Override
    public EntityModel<Folder> toModel(Folder folder) {
        return EntityModel.of(folder,
                linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel(),
                linkTo(methodOn(FolderController.class).getAllFolders()).withRel("folders"));
    }
}