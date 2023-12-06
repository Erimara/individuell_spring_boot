package com.example.individuell.services;

import com.example.individuell.Assemblers.FileDtoModelAssembler;
import com.example.individuell.Assemblers.FileModelAssembler;
import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.Exceptions.ForbiddenActionException;
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

/**
 * Service class which handles the logic for CRUD methods regarding the file-model class
 */
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

    /**
     * Method uploads a single "free" multipartFile and creates a hashmap to store the content.
     * The current logged-in user in the client will be set as the owner of the file
     * generateFile() is called upon which is a helper method
     *
     * @param file is a part of the class MultipartFile
     * @return File
     * @throws IOException handles errors when uploading multipartFiles
     */
    public File uploadSingleFile(MultipartFile file) throws IOException {
        HashMap<String, String> fileMap = new HashMap<>();
        String getUsername = userRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(getUsername);
        File savedFile = generateFile(file, fileMap, user);
        fileRepository.save(savedFile);
        return savedFile;
    }


    /**
     * Method uploads a multipartFile to a specific folder and creates a hashmap to store the content.
     * The current logged-in user in the client will be set as the owner of the file
     * generateFile() is called upon which is a helper method
     *
     * @param file is a part of the class MultipartFile
     * @param id   gets the selected folders id
     * @return File
     * @throws IOException handles errors when uploading multipartFiles
     */

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

    /**
     * Helper method to help generate files for uploading them
     *
     * @param file    refers to the multipartFiles class
     * @param fileMap holds all the content of the file
     * @param user    is used to set the current logged-in user to the uploaded file.
     * @return File
     * @throws IOException handles errors when uploading multipartFiles
     */
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

    /**
     * Gets all the files. Only admins can access these
     *
     * @return List<Files>
     */
    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    /**
     * Method blocks users from seeing other peoples files, and enables to only see your own
     *
     * @param username gets the current logged-in user
     * @return List<FileDto>
     */
    @Cacheable(value = "my-files")
    public List<FileDto> viewMyFiles(String username) {
        User user = userRepository.findByEmail(username);
        return fileRepository.findAll()
                .stream()
                .filter(x -> Objects.equals(x.getFileOwner().getId(), user.getId()))
                .map((file) -> new FileDto(file.getId(), file.getFileProperties(), file.getFileOwner().getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Method downloads a specific file.
     *
     * @param id finds the id that you want to download
     * @return ByteArrayResource which is displayed in postman as Bytes
     * @throws FileNotFoundException is a custom error for throwing a unique error
     */

    public ByteArrayResource downloadFile(String id) throws FileNotFoundException {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Could not find file with id:" + id));
        byte[] bytes = file.getFileProperties().get("Bytes: ").getBytes();
        return new ByteArrayResource(bytes);
    }

    /**
     * Method to get a specific file. Only Admin access at the moment.
     *
     * @param id finds the specific file by id
     * @return File
     * @throws FileNotFoundException is a custom error for throwing a unique error
     */
    public File getFileById(String id) throws FileNotFoundException, ForbiddenActionException {
      File file = fileRepository.findById(id).orElseThrow(() -> new FileNotFoundException("Could not find file with id:" + id));
      String loggedInUser = userRepository.getLoggedInUser().getName();
        if (file.getFileOwner().getEmail().equals(loggedInUser)) {
            return file;
        } else throw new ForbiddenActionException("Restricted access");
    }

    /**
     * Deletes a file by id.
     *
     * @param id fins the id of the specific file
     */

    public void deleteFileById(String id) throws FileNotFoundException, ForbiddenActionException {
        String loggedInUser = userRepository.getLoggedInUser().getName();
        File file = fileRepository.findById(id).orElseThrow(() -> new FileNotFoundException("Could not find file with id:" + id));
        if (file.getFileOwner().getEmail().equals(loggedInUser)) {
            fileRepository.deleteById(id);
        } else throw new ForbiddenActionException("Restriction access");
    }
}