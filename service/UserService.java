package service;

import model.Role;
import model.UserDTO;
import repository.api.IUserRepository;
import repository.entity.User;
import service.api.IUserService;
import service.api.IUserValidator;

public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IUserValidator userValidator;

    public UserService(IUserRepository userRepository, IUserValidator validator) {
        this.userRepository = userRepository;
        this.userValidator = validator;
    }

    @Override
    public User login(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
        final String login = userDTO.getLogin().trim();
        userValidator.validateLogin(login);

        return userRepository.find(login)
                .orElseGet(() -> {
                    Role role = isAdmin(login) ? Role.ADMIN : Role.CUSTOMER;
                    User user = new User(role, login);
                    userRepository.add(user);
                    return user;
                });
    }

    private boolean isAdmin(String login) {
        return "admin".equalsIgnoreCase(login);
    }
}
