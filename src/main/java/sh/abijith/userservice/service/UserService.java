package sh.abijith.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sh.abijith.userservice.dto.PagedUserResponse;
import sh.abijith.userservice.dto.UpdateUserRequest;
import sh.abijith.userservice.dto.UserProfileRequest;
import sh.abijith.userservice.dto.UserProfileResponse;
import sh.abijith.userservice.exception.UserAlreadyExistsException;
import sh.abijith.userservice.exception.UserNotFoundException;
import sh.abijith.userservice.mapper.UserMapper;
import sh.abijith.userservice.model.Role;
import sh.abijith.userservice.model.User;
import sh.abijith.userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Retrieves a user by ID if the account is enabled.
     *
     * @param id the user's ID
     * @return user profile response
     */
    public UserProfileResponse getUserById(String id) {
        return userRepository.findByIdAndEnabledTrue(id)
                .map(userMapper::toUserProfileResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    /**
     * Retrieves a user by email if the account is enabled.
     *
     * @param email the user's email
     * @return user profile response
     */
    public UserProfileResponse getUserByEmail(String email) {
        return userRepository.findByEmailAndEnabledTrue(email)
                .map(userMapper::toUserProfileResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Creates a new user profile. Fails if email is already in use.
     *
     * @param request user profile request
     * @return the new user's ID
     */
    public String createUser(UserProfileRequest request) {
        if (userRepository.findByEmailAndEnabledTrue(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = userMapper.toUser(request);
        user.setEnabled(true);
        user.setRoles(Set.of(Role.USER));
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user).getId();
    }

    /**
     * Updates a user's details
     *
     * @param id      the user's ID
     * @param request the update request
     */
    public void updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setCustomAttributes(request.getCustomAttributes());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * Soft deletes (disables) a user account.
     *
     * @param id the user's ID
     */
    public void deactivateUser(String id) {
        User user = userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Reactivates a disabled user account.
     *
     * @param id the user's ID
     */
    public void reactivateUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Retrieves a paginated list of all enabled users.
     *
     * @param pageable the pagination and sorting parameters
     * @return paged user response
     */
    public PagedUserResponse getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAllByEnabledTrue(pageable);
        List<UserProfileResponse> users = page.getContent().stream()
                .map(userMapper::toUserProfileResponse)
                .toList();

        return PagedUserResponse.builder()
                .users(users)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();

    }


    /**
     * Performs a case-insensitive regex search across first name, last name, and email.
     *
     * @param searchTerm the keyword to search
     * @return list of matching user profiles
     */
    public List<UserProfileResponse> searchUsers(String searchTerm) {
        String regex = "(?i).*" + searchTerm + ".*";

        List<User> uniqueMatches = Stream.of(
                        userRepository.findByFirstNameRegex(regex),
                        userRepository.findByLastNameRegex(regex),
                        userRepository.findByEmailRegex(regex)
                )
                .flatMap(List::stream)
                .distinct()
                .toList();

        return uniqueMatches.stream().map(userMapper::toUserProfileResponse).toList();
    }

}
