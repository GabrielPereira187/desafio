package br.com.desafio.service;

import br.com.desafio.entity.User;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.repository.UserRepository;
import br.com.desafio.util.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    User user;

    @BeforeEach
    public void setUp() {
        user = UserCreator.createAdminUser();
    }

    @Test
    void shouldFindUser() throws UserNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User user = userService.getUser(1L);

        assertNotNull(user);
    }

    @Test
    void shouldNotFindUser() throws UserNotFoundException {
        final UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(2L);
        });

        assertThat(e, notNullValue());
    }

    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        User userTest = (User) userService.findByEmail(user.getEmail());

        assertNotNull(userTest);
    }

    @Test
    void shouldNotFindUserByEmail() {
        when(userRepository.findByEmail("teste2@gmail.com")).thenReturn(null);

        User user = (User) userService.findByEmail("teste2@gmail.com");

        assertNull(user);
    }



}
