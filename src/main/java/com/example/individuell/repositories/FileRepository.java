package com.example.individuell.repositories;

import com.example.individuell.models.File;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, String> {

}
