package com.example.individuell.Assemblers;


import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.controllers.FileController;
import com.example.individuell.models.File;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FileDtoModelAssembler implements RepresentationModelAssembler<FileDto, EntityModel<FileDto>>{
    @Override
    public EntityModel<FileDto> toModel(FileDto fileDto) {
        try {
            return EntityModel.of(fileDto,
                    linkTo(methodOn(FileController.class).getFileById(fileDto.getId())).withSelfRel(),
                    linkTo(methodOn(FileController.class).getAllFiles()).withRel("files"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

