package com.example.individuell.Assemblers;


import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.Exceptions.ForbiddenActionException;
import com.example.individuell.controllers.FileController;
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
public class FileDtoModelAssembler implements RepresentationModelAssembler<FileDto, EntityModel<FileDto>>{
    /**
     * @Override method which exists in RepresentationModelAssembler. It adds links to whatever is shown from the database or saved to the database
     * @param fileDto represents the FileDTO class
     * @return EntityModel<FileDto>
     */
    @Override
    public EntityModel<FileDto> toModel(FileDto fileDto) {
        try {
            return EntityModel.of(fileDto,
                    linkTo(methodOn(FileController.class).getFileById(fileDto.getId())).withSelfRel(),
                    linkTo(methodOn(FileController.class).getAllFiles()).withRel("files"));
        } catch (FileNotFoundException | ForbiddenActionException e) {
            throw new RuntimeException(e);
        }
    }
}

