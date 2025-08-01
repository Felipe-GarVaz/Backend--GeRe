package com.demo.GeVi.controller;

import com.demo.GeVi.model.FailType;
import com.demo.GeVi.repository.FailTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/failTypes")
public class FailTypeController {

    @Autowired
    private FailTypeRepository failTypeRepository;

    @GetMapping
    public List<FailType> getAllFailTypes() {
        return failTypeRepository.findAll();
    }
}
