package sh.abijith.userservice.mapper;

import org.springframework.stereotype.Component;
import sh.abijith.userservice.dto.UserProfileRequest;
import sh.abijith.userservice.dto.UserProfileResponse;
import sh.abijith.userservice.model.User;

@Component
public class UserMapper {

    public UserProfileResponse toUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .roles(user.getRoles())
                .customAttributes(user.getCustomAttributes())
                .build();
    }

    public User toUser(UserProfileRequest request) {
        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .customAttributes(request.getCustomAttributes())
                .build();
    }
}
