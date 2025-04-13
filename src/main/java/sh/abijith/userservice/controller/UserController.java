package sh.abijith.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.abijith.userservice.dto.PagedUserResponse;
import sh.abijith.userservice.dto.UpdateUserRequest;
import sh.abijith.userservice.dto.UserProfileRequest;
import sh.abijith.userservice.dto.UserProfileResponse;
import sh.abijith.userservice.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the user's ID
     * @return user profile response
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the user's email
     * @return user profile response
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Updates the details of a user.
     *
     * @param id      the user's ID
     * @param request the update request
     * @return 204 No Content on success
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates (soft deletes) a user account.
     *
     * @param id the user's ID
     * @return 204 No Content on success
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable String id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reactivates a deactivated user account.
     *
     * @param id the user's ID
     * @return 204 No Content on success
     */
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateUser(@PathVariable String id) {
        userService.reactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param page   the page number (default 0)
     * @param size   the page size (default 10)
     * @param sortBy the sorting field (default: email)
     * @return paged list of user profiles
     */
    @GetMapping
    public ResponseEntity<PagedUserResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "email") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    /**
     * Searches users by name or email using regex.
     *
     * @param query the search keyword
     * @return list of matched user profiles
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam String query) {
        List<UserProfileResponse> results = userService.searchUsers(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Creates a new user. This is typically called from the Auth Service.
     *
     * @param request user profile data
     * @return 201 Created with location header
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserProfileRequest request) {
        String userId = userService.createUser(request);
        URI location = URI.create("/users/" + userId);
        return ResponseEntity.created(location).build();
    }
}
