package app.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class ApiKeyAuthentication implements Authentication
{
    private String apiKey;
    private boolean authenticated = true;

    public ApiKeyAuthentication(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of();
    }

    @Override
    public Object getCredentials()
    {
        return apiKey;
    }

    @Override
    public Object getDetails()
    {
        return null;
    }

    @Override
    public Object getPrincipal()
    {
        return apiKey;
    }

    @Override
    public boolean isAuthenticated()
    {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
    {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName()
    {
        return "API-Key";
    }
}
