package sh.abijith.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedUserResponse {
    private List<UserProfileResponse> users;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
