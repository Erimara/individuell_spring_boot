package com.example.individuell.repositories;

import com.example.individuell.models.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FolderRepository extends MongoRepository<Folder,String> {
}
