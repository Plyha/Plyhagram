package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.enums.EnumRole;
import com.example.demo.exeptions.UserExistExeption;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User createUser(SignupRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.getRoles().add(EnumRole.ROLE_USER);

        try {
            LOG.info("Saving User {}", user.getUsername());
            return userRepository.save(user);

        } catch (Exception e){
            LOG.error("Error registration. {}", e.getMessage());
            throw new UserExistExeption("The user " + user.getUsername() + " already exist!");
        }
    }
}
