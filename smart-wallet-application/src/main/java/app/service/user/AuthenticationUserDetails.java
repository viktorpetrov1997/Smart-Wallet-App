package app.service.user;

import app.model.entity.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AuthenticationUserDetails implements UserDetails
{
    private UUID id;
    private String username;
    private String password;
    private UserRole role;
    private boolean isActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return isActive;
    }

    @Override
    public boolean isEnabled()
    {
        return isActive;
    }
}
