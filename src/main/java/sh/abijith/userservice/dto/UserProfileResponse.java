package sh.abijith.userservice.dto;

import lombok.Builder;
import lombok.Data;
import sh.abijith.userservice.model.Role;

import java.util.Map;
import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean enabled;
    private Set<Role> roles;
    private Map<String, Object> customAttributes;
}
