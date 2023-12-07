package com.example.individuell.services;

import com.example.individuell.Assemblers.FolderDtoModelAssembler;
import com.example.individuell.Assemblers.FolderModelAssembler;
import com.example.individuell.DTOS.FileInFolderDto;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.Exceptions.ForbiddenActionException;
import com.example.individuell.Exceptions.NotFoundException;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FolderRepository;
import com.example.individuell.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service class which handles the logic for CRUD methods regarding the folder-model class
 */
@Service
public class FolderService {
    FolderRepository folderRepository;
    UserRepository userRepository;
    FolderModelAssembler assembler;
    FolderDtoModelAssembler dtoAssembler;
    AuthenticationProvider authenticationProvider;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserRepository userRepository, FolderModelAssembler assembler, FolderDtoModelAssembler dtoAssembler, AuthenticationProvider authenticationProvider) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.dtoAssembler = dtoAssembler;
        this.authenticationProvider = authenticationProvider;
    }

    /**
     * creates a folder and sets the current logged-in user as a folderOwner
     *
     * @param folder takes in json data which enables you to create objects that look like the folder-model
     * @return Folder
     */
    public Folder createFolder(Folder folder) {
        String username = userRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(username);
        folder.setFolderOwner(user);
        folderRepository.save(folder);
        return folder;
    }

    /**
     * Method for getting all the folders in database. Admin only access
     *
     * @return List<Folder>
     */
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    /**
     * Method blocks users from seeing other peoples folders, and enables to only see your own
     *
     * @param username gets the current logged-in user to be able to filter out other users
     * @return List<FolderDto>
     */
    @Cacheable(value = "my-folders")
    public List<FolderDto> viewMyFolders(String username) {
        User user = userRepository.findByEmail(username);
        return folderRepository.findAll()
                .stream()
                .filter(x -> Objects.equals(x.getFolderOwner().getId(), user.getId()))
                .map(folder -> {
                    List<FileInFolderDto> fileInFolderDto = folder.getMyFiles().stream()
                            .filter(Objects::nonNull)
                            .map(file -> new FileInFolderDto(file.getId(), file.getName())).toList();
                    return new FolderDto(folder.getId(),
                            folder.getFolderName(),
                            folder.getFolderOwner().getEmail(),
                            new ArrayList<>(fileInFolderDto));
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets a specific folder by id. Admin only access atm.
     *
     * @param id finds the specific folder
     * @return Folder
     * @throws NotFoundException is a custom error for throwing a unique error
     * @throws ForbiddenActionException is a custom error for throwing a unique error
     */
    public Folder getFolderById(String id) throws NotFoundException, ForbiddenActionException {
        String loggedInUser = userRepository.getLoggedInUser().getName();
        Folder folder =  folderRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Could not find folder with id:" + id));
        if (folder.getFolderOwner().getEmail().equals(loggedInUser)){
            return folder;
        } else throw new ForbiddenActionException("Restricted access");

    }
    /**
     * Deletes a folder by id.
     *
     * @param id finds the id of the specific folder
     * @throws NotFoundException is a custom error for throwing a unique error
     * @throws ForbiddenActionException is a custom error for throwing a unique error
     */
    public void deleteFolderById(String id) throws ForbiddenActionException, NotFoundException {
        String loggedInUser = userRepository.getLoggedInUser().getName();
        Folder folder =  folderRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Could not find folder with id:" + id));
        if (folder.getFolderOwner().getEmail().equals(loggedInUser)) {
            folderRepository.deleteById(id);
        } else throw new ForbiddenActionException("Restricted access");
    }


}
