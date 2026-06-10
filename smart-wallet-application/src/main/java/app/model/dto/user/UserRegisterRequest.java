package app.model.dto.user;

import app.model.entity.user.Country;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterRequest
{
    String username;
    String password;
    Country country;
}
