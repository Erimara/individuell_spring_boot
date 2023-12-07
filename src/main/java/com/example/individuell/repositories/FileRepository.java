package com.example.individuell.repositories;

import com.example.individuell.models.File;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface for enabling built-in mongoDB to communicate with the database
 */
public interface FileRepository extends MongoRepository<File, String> {

}
