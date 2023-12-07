package com.example.individuell.Assemblers;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.Exceptions.ForbiddenActionException;
import com.example.individuell.Exceptions.NotFoundException;
import com.example.individuell.controllers.FolderController;
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
public class FolderDtoModelAssembler implements RepresentationModelAssembler<FolderDto, EntityModel<FolderDto>>{
    /**
     * @Override method which exists in RepresentationModelAssembler. It adds links to whatever is shown from the database or saved to the database
     * Catches eventual failures where id is not found and throws runtime exception
     * @param folderDto represents the FolderDTO class
     * @return EntityModel<folderDto>
     */
    @Override
    public EntityModel<FolderDto> toModel(FolderDto folderDto) {
        try {
            return EntityModel.of(folderDto,
                    linkTo(methodOn(FolderController.class).getFolderById(folderDto.getId())).withSelfRel(),
                    linkTo(methodOn(FolderController.class).getAllFolders()).withRel("folders"));
        } catch (NotFoundException e) {
            throw new RuntimeException("Could not assemble folderDTO, folder not found");
        } catch (ForbiddenActionException e) {
            throw new RuntimeException("A forbidden request was made");
        }
    }
}

