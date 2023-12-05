package com.example.individuell.services;

import com.example.individuell.Assemblers.FolderDtoModelAssembler;
import com.example.individuell.Assemblers.FolderModelAssembler;
import com.example.individuell.DTOS.FileInFolderDto;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.Exceptions.FolderNotFoundException;
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

    public Folder createFolder(Folder folder) {
        String username = userRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(username);
        folder.setFolderOwner(user);
        folderRepository.save(folder);
        return folder;
    }
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }
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
    public Folder getFolderById(String id) throws FolderNotFoundException {
        return folderRepository.findById(id).orElseThrow(() ->
                new FolderNotFoundException("Could not find folder with id:" + id));

    }
    public void deleteFolderById(String id) {
        folderRepository.deleteById(id);
    }
}
