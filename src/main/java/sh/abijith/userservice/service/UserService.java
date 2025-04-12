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
import sh.abijith.userservice.model.User;
import sh.abijith.userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getUserById(String id) {
        return userRepository.findByIdAndEnabledTrue(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public UserProfileResponse getUserByEmail(String email) {
        return userRepository.findByEmailAndEnabledTrue(email)
                .map(this::mapToResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public String createUser(UserProfileRequest dto) {
        if (userRepository.findByEmailAndEnabledTrue(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = new User(null, dto.getEmail(), dto.getFirstName(), dto.getLastName(), true);
        user = userRepository.save(user);
        return user.getId();
    }

    public void updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.save(user);
    }

    public void deactivateUser(String id) {
        User user = userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found or already deactivated"));

        user.setEnabled(false);
        userRepository.save(user);
    }

    public void reactivateUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    public PagedUserResponse getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAllByEnabledTrue(pageable);

        List<UserProfileResponse> users = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedUserResponse.builder()
                .users(users)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private UserProfileResponse mapToResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .build();
    }
}
