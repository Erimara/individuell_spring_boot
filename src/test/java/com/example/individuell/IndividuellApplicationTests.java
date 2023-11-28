package com.example.individuell;

import com.example.individuell.models.File;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.services.FileService;
import com.example.individuell.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
* 1: Test to see user registration
* 2: Test to see if a file is getting uploaded
* 3: Test to see if file can be deleted
*/



@RunWith(SpringRunner.class)
@SpringBootTest
class IndividuellApplicationTests {

    UserService userService;
    UserRepository userRepository;

    FileService fileService;
    FileRepository fileRepository;
    MongoTemplate mongoTemplate;

    @Autowired
    public IndividuellApplicationTests(UserService userService, UserRepository userRepository,
                                       FileService fileService, FileRepository fileRepository,
                                       MongoTemplate mongoTemplate)
    {
        this.userService = userService;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.fileRepository = fileRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @Test
    void contextLoads() {

    }



    @Test
    void userRegistration() {
        //Create user
        User testUser = new User();
        testUser.setEmail("Hello@world.se");
        testUser.setPassword("hello-word");
        //Save user
        mongoTemplate.save(testUser);

        //Find user
        User user = userRepository.findByEmail(testUser.getEmail());

        //Assert user
        assertNotNull(user);
        System.out.println("Saved User: " + testUser);
        System.out.println("Retrieved User: " + user);
    }


}
