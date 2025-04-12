package sh.abijith.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.abijith.userservice.dto.UserProfileRequest;
import sh.abijith.userservice.dto.UserProfileResponse;
import sh.abijith.userservice.service.UserService;

import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<UserProfileRequest> createUser(@RequestBody @Valid UserProfileRequest request) {
        String userId = userService.createUser(request);
        URI location = URI.create("/users/" + userId);
        return ResponseEntity.created(location).build();
    }
}
