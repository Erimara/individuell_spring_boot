package com.example.individuell.Assemblers;


import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.controllers.FileController;
import com.example.individuell.models.File;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
/**
 * Component class which helps to assemble the created and the showed items in different classes.
 * The class adds links to itself and all others of its class items for easy access
 */
@Component
public class FileModelAssembler implements RepresentationModelAssembler<File, EntityModel<File>>{
    /**
     * @Override method which exists in RepresentationModelAssembler. It adds links to whatever is shown from the database or saved to the database
     * @param file
     * @return EntityModel<File>
     */
    @Override
    public EntityModel<File> toModel(File file) {
        try {
            return EntityModel.of(file,
                    linkTo(methodOn(FileController.class).getFileById(file.getId())).withSelfRel(),
                    linkTo(methodOn(FileController.class).getAllFiles()).withRel("files"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

