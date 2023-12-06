package com.example.individuell.Assemblers;

import com.example.individuell.Exceptions.FolderNotFoundException;
import com.example.individuell.Exceptions.ForbiddenActionException;
import com.example.individuell.controllers.FolderController;
import com.example.individuell.models.Folder;
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
public class FolderModelAssembler implements RepresentationModelAssembler<Folder, EntityModel<Folder>> {
    /**
     * @Override method which exists in RepresentationModelAssembler. It adds links to whatever is shown from the database or saved to the database
     * @param folder represents the Folder-model class
     * @return EntityModel<folder>
     */
    @Override
    public EntityModel<Folder> toModel(Folder folder) {
        try {
            return EntityModel.of(folder,
                    linkTo(methodOn(FolderController.class).getFolderById(folder.getId())).withSelfRel(),
                    linkTo(methodOn(FolderController.class).getAllFolders()).withRel("folders"));
        } catch (FolderNotFoundException e) {
            throw new RuntimeException("Could not assemble Folder");
        } catch (ForbiddenActionException e) {
            throw new RuntimeException(e);
        }
    }
}