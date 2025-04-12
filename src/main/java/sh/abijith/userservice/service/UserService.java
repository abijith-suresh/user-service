package sh.abijith.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sh.abijith.userservice.dto.UserProfileRequest;
import sh.abijith.userservice.exception.UserAlreadyExistsException;
import sh.abijith.userservice.exception.UserNotFoundException;
import sh.abijith.userservice.model.User;
import sh.abijith.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileRequest getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserProfileRequest createUser(UserProfileRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = new User(null, dto.getEmail(), dto.getFirstName(), dto.getLastName(), true, false);
        user = userRepository.save(user);
        return toDto(user);
    }

    private UserProfileRequest toDto(User user) {
        return new UserProfileRequest(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
