package com.example.individuell;
import com.example.individuell.models.File;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.userdetails.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
class IndividuellApplicationTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    CustomUserDetails userDetails;
    @Test
    void userRegistration() {
        User testUser = new User();
        testUser.setId("test-id-user");
        testUser.setEmail("Hello@world.se");
        testUser.setPassword("hello-world");

        userRepository.save(testUser);
        User user = userRepository.findById(testUser.getId()).orElseThrow(()-> new RuntimeException("Could not find user"));

        Assertions.assertNotNull(user);
        Assertions.assertTrue(userRepository.existsById(user.getId()));
    }

    @Test
    void uploadFile() throws Exception {
        HashMap<String, String> fileMap = new HashMap<>();
        byte[] fileContent = "Testing file upload".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", fileContent);
        fileMap.put("Filename: ", mockFile.getOriginalFilename());
        fileMap.put("Bytes: ", mockFile.getBytes().toString());
        fileMap.put("File type: ", mockFile.getContentType());
        File savedFile = new File();
        savedFile.setFileProperties(fileMap);
        savedFile.setId("test-id-file");

        User mockUser = new User();
        mockUser.setId("test-id-user");
        mockUser.setEmail("Hello@world.se");
        mockUser.setPassword("hello-world");
        savedFile.setFileOwner(mockUser);


        fileRepository.save(savedFile);
        File retrieveSavedFile = fileRepository.findById(savedFile.getId()).orElseThrow(() -> new RuntimeException("File not found"));
        Assertions.assertTrue(fileRepository.existsById(retrieveSavedFile.getId()));
    }
    @Test
    void removeFile(){
        File testFile = new File();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("key","value");
        testFile.setId("test-id-remove-file");
        testFile.setFileProperties(hashMap);
        fileRepository.save(testFile);
        fileRepository.findById("test-id-remove-file").ifPresent(presentFile -> fileRepository.deleteById(presentFile.getId()));
        Assertions.assertFalse(fileRepository.existsById(testFile.getId()));
    }

    @Test
    void loginMultipleUsers(){
            userDetails.loadUserByUsername("erik@email.se");
        };
    @AfterEach
    void removeTestData(){
        fileRepository.findById("test-id-file").ifPresent(presentFile -> fileRepository.deleteById(presentFile.getId()));
        userRepository.findById("test-id-user").ifPresent(presentUser -> userRepository.deleteById(presentUser.getId()));
    }
}
