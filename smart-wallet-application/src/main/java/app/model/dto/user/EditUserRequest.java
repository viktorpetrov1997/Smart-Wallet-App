package app.model.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EditUserRequest
{

    private String firstName;
    private String lastName;
    private String profilePicture;
    private String email;
}
