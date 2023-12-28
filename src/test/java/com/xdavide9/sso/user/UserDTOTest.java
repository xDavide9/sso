package com.xdavide9.sso.user;

// Unit test that checks that UserDTO transfers fields correctly

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserDTOTest {
    @Test
    void itShouldCreateDTOCorrectly() {
        // given
        User u = new User();
        u.setUsername("username");
        u.setEmail("email");
        u.setPhoneNumber("phoneNumber");
        u.setRole(Role.USER);
        u.setEnabled(true);
        u.setAccountNonExpired(true);
        u.setAccountNonLocked(true);
        u.setCredentialsNonExpired(true);
        // when
        UserDTO dto = UserDTO.fromUser(u);
        // then
        assertThat(dto.getUsername()).isEqualTo(u.getUsername());
        assertThat(dto.getEmail()).isEqualTo(u.getEmail());
        assertThat(dto.getPhoneNumber()).isEqualTo(u.getPhoneNumber());
        assertThat(dto.getRole()).isEqualTo(u.getRole());
        assertThat(dto.isAccountNonExpired()).isEqualTo(u.isAccountNonExpired());
        assertThat(dto.isAccountNonLocked()).isEqualTo(u.isAccountNonLocked());
        assertThat(dto.isEnabled()).isEqualTo(u.isEnabled());
        assertThat(dto.isCredentialsNonExpired()).isEqualTo(u.isCredentialsNonExpired());
    }
}