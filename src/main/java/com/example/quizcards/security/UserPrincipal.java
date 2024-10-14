package com.example.quizcards.security;

import com.example.quizcards.entities.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    @JsonIgnore
    private String userCode;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private boolean isAccountNonExpired;

    @JsonIgnore
    private boolean isAccountNonLocked;

    @JsonIgnore
    private boolean isCredentialsNonExpired;

    @JsonIgnore
    private boolean isEnabled;

    private Collection<? extends GrantedAuthority> authorities;


    public static UserPrincipal create(AppUser user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()));

        return new UserPrincipal(user.getUserId(),
                user.getFirstName(), user.getLastName(), user.getUsername(), user.getUserCode(),
                user.getEmail(), user.getHashPassword(), user.getAccountNonExpired(),
                user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getEnabled(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities == null ? null : new ArrayList<>(authorities);
    }
}
