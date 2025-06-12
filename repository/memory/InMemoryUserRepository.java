package repository.memory;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.*;

public class InMemoryUserRepository implements IUserRepository {
    private final List<User> users = new ArrayList<>();
    private final Map<String, User> loginIndex = new HashMap<>();
    private static final String ADMIN_LOGIN = "Admin";

    public InMemoryUserRepository() {
        ensureAdminExists();
    }

    @Override
    public User add(User user) {
        if (user == null || user.getLogin() == null) {
            throw new IllegalArgumentException("User or login cannot be null");
        }
        users.add(user);
        loginIndex.put(user.getLogin().toLowerCase(), user);
        return user;
    }

    @Override
    public Optional<User> find(String login) {
        if (login == null || login.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(loginIndex.get(login.trim().toLowerCase()));
    }

    private boolean adminExists() {
        return users.stream()
                .anyMatch(this::isAdmin);
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private void ensureAdminExists() {
        if (!adminExists()) {
            User admin = new User(Role.ADMIN, ADMIN_LOGIN);
            users.add(admin);
            loginIndex.put(admin.getLogin().toLowerCase(), admin);
        }
    }
}
