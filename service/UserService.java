package service;

import model.Role;
import model.UserDTO;
import repository.api.IUserRepository;
import repository.entity.User;
import service.api.IUserService;
import service.api.IUserValidator;

import java.util.Optional;

public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IUserValidator userValidator;

    public UserService(IUserRepository userRepository, IUserValidator validator) {
        this.userRepository = userRepository;
        this.userValidator = validator;
    }

    @Override
    public User login(UserDTO userDTO) {
        String login = userDTO.getLogin();
        userValidator.validateLogin(login);

        login = login.trim();

        Optional<User> optionalUser = userRepository.find(login);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        Role role = isAdmin(login) ? Role.ADMIN : Role.CUSTOMER;
        User user = new User(role, login);
        userRepository.add(user);
        return user;
    }

    private boolean isAdmin(String login) {
        return "admin".equalsIgnoreCase(login);
    }
}
