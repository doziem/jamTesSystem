package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.model.Staff;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.StaffRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role effectiveRole = staffRepository.findByUser_Id(user.getId())
                .map(Staff::getRole)
                .orElse(Role.STAFF);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(effectiveRole.name())
                .build();
    }
}
