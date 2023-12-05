package com.example.individuell.services;

import com.example.individuell.Assemblers.FileDtoModelAssembler;
import com.example.individuell.Assemblers.FileModelAssembler;
import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.models.File;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.repositories.FolderRepository;
import com.example.individuell.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    FileRepository fileRepository;
    UserRepository userRepository;
    FileModelAssembler assembler;
    FileDtoModelAssembler dtoAssembler;
    FolderRepository folderRepository;

    @Autowired
    public FileService(FileRepository fileRepository, UserRepository userRepository, FileModelAssembler assembler, FileDtoModelAssembler dtoAssembler, FolderRepository folderRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.dtoAssembler = dtoAssembler;
        this.folderRepository = folderRepository;
    }

    public File uploadSingleFile(MultipartFile file) throws IOException {
        HashMap<String, String> fileMap = new HashMap<>();
        String getUsername = userRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(getUsername);
        File savedFile = generateFile(file, fileMap, user);
        fileRepository.save(savedFile);
       return savedFile;
    }

    public EntityModel<File> uploadFileToFolder(MultipartFile file, String id) throws IOException {
        HashMap<String, String> fileMap = new HashMap<>();
        String getUsername = userRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(getUsername);
        Folder folder = folderRepository.findById(id).orElseThrow(RuntimeException::new);

        File savedFile = generateFile(file, fileMap, user);

        folder.getMyFiles().add(savedFile);
        fileRepository.save(savedFile);
        folderRepository.save(folder);
        return assembler.toModel(savedFile);
    }

    private File generateFile(MultipartFile file, HashMap<String, String> fileMap, User user) throws IOException {
        File savedFile = new File();
        fileMap.put("Filename: ", file.getOriginalFilename());
        fileMap.put("Bytes: ", Base64.getEncoder().encodeToString(file.getBytes()));
        fileMap.put("File type: ", file.getContentType());
        savedFile.setFileOwner(user);
        savedFile.setFileProperties(fileMap);
        savedFile.setName(file.getOriginalFilename());
        return savedFile;
    }

    public List<File> getAllFiles() {
      return fileRepository.findAll();
    }

    @Cacheable(value = "my-files")
    public List<FileDto> viewMyFiles(String username) {
        User user = userRepository.findByEmail(username);
        return fileRepository.findAll()
                .stream()
                .filter(x -> Objects.equals(x.getFileOwner().getId(), user.getId()))
                .map((file) -> new FileDto(file.getId(), file.getFileProperties(), file.getFileOwner().getEmail()))
                .collect(Collectors.toList());
    }

    public ByteArrayResource downloadFile(String id) throws FileNotFoundException {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Could not find file with id:" + id));
        byte[] bytes = file.getFileProperties().get("Bytes: ").getBytes();
        return new ByteArrayResource(bytes);
    }

    public File getFileById(String id) throws FileNotFoundException {
        return fileRepository.findById(id).orElseThrow(() ->
                new FileNotFoundException("Could not find file with id:" + id));
    }

    public void deleteFileById(String id) {
        fileRepository.deleteById(id);
    }

}