package com.example.MyPersonalContactManager.service;

import com.example.MyPersonalContactManager.exceptions.UserAlreadyExistsException;
import com.example.MyPersonalContactManager.models.UserModels.*;
import com.example.MyPersonalContactManager.repository.InterfaceUserRepository;
import com.example.MyPersonalContactManager.utils.UtilsRegistration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DataBaseUserService implements InterfaceUserService {

    private final InterfaceUserRepository<User> userRepository;
    private UtilsRegistration utilsRegistration;

    @Override
    public UserDTOResponse registerUser(UserDTORegister userDTORegister) {
        Optional<User> existingUser = userRepository.getUserByLogin(userDTORegister.getLogin());
        if (utilsRegistration.checkExistingUser(existingUser)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User newUser = new User();
        newUser.setLogin(userDTORegister.getLogin());
        newUser.setPassword(userDTORegister.getPassword());
        newUser.setUserName(userDTORegister.getName());
        newUser.setRole(false);

        userRepository.createUser(newUser);

        String token = utilsRegistration.generateToken(newUser.getLogin(), newUser.getPassword());
        userRepository.saveToken(token, String.valueOf(newUser.getUserId()));
        return new UserDTOResponse(newUser.getLogin(), token);
    }

    @Override
    public Optional<UserToken> loginUser(UserDTOLogin userDTOLogin) {
        Optional<User> existingUser = userRepository.getUserByLogin(userDTOLogin.getLogin());
        if (existingUser.isEmpty() || !existingUser.get().getPassword().equals(userDTOLogin.getPassword())) {
            throw new RuntimeException("Invalid login or password.");
        }
        String token = utilsRegistration.generateToken(existingUser.get().getLogin(), existingUser.get().getPassword());
        return Optional.empty();
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public boolean deleteUserById(String userId) {
        return userRepository.deleteUserById(userId);
    }

    @Override
    public UserDTOResponse updateUser(User user) {
        return null;
    }

    @Override
    public String generateToken(String login, String password) {
        return utilsRegistration.generateToken(login, password);
    }
}
