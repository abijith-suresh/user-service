package sh.abijith.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Map<String, Object> customAttributes;
}
