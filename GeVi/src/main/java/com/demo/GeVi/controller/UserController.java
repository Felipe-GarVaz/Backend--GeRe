package com.demo.GeVi.controller;

import com.demo.GeVi.dto.UserCreateRequestDTO;
import com.demo.GeVi.dto.UserResponseDTO;
import com.demo.GeVi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateRequestDTO request) throws Exception {
        var resp = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query) throws Exception {
        List<UserResponseDTO> list = userService.searchByRpe(query);
        if (list.isEmpty())
            return ResponseEntity.noContent().build(); // 204
        return ResponseEntity.ok(list); // 200 List<DTO>
    }

    // DELETE /api/user/rpe/{rpe}
    @DeleteMapping("/rpe/{rpe}")
    public ResponseEntity<Void> deleteByRpe(@PathVariable String rpe) throws Exception {
        userService.deleteByRpe(rpe);
        return ResponseEntity.noContent().build(); // 204
    }
}
