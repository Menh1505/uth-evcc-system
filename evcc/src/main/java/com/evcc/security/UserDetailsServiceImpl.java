package com.evcc.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.user.entity.Role;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        return UserPrincipal.create(user);
    }

    // Inner class để implement UserDetails
    public static class UserPrincipal implements UserDetails {
        private String username;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;

        public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
            this.username = username;
            this.password = password;
            this.authorities = authorities;
        }

        public static UserPrincipal create(User user) {
            Collection<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());
            return new UserPrincipal(user.getUsername(), user.getPassword(), authorities);
        }

        private static Collection<GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
            if (roles == null) {
                return Set.of(new SimpleGrantedAuthority("ROLE_USER"));
            }
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toList());
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}