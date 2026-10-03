package app.model.dto.user;

import app.model.entity.user.Country;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data

@Builder
public class UserRegisterRequest
{
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Country must not be null")
    private Country country;
}
