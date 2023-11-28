package com.example.individuell.Assemblers;


import com.example.individuell.DTOS.FileDto;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.Exceptions.FolderNotFoundException;
import com.example.individuell.controllers.FileController;
import com.example.individuell.controllers.FolderController;
import com.example.individuell.models.File;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FolderDtoModelAssembler implements RepresentationModelAssembler<FolderDto, EntityModel<FolderDto>>{
    @Override
    public EntityModel<FolderDto> toModel(FolderDto folderDto) {
            return EntityModel.of(folderDto,
                    linkTo(methodOn(FolderController.class).getFolderById(folderDto.getId())).withSelfRel(),
                    linkTo(methodOn(FolderController.class).getAllFolders()).withRel("folders"));
    }
}
