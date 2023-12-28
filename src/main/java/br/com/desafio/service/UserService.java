package br.com.desafio.service;


import br.com.desafio.entity.User;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public String getUserName(Long userId) {
        log.info("Buscando usuario com id:{}", userId);

        return userRepository.findUsernameById(userId);
    }

    public UserDetails findByEmail(String email) {
        log.info("Buscando usuario com email:{}", email);

        return userRepository.findByEmail(email);
    }

}
