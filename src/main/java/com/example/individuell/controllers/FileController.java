package com.example.individuell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    private final StorageService storageService;
    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }


}
