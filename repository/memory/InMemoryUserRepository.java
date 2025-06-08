package repository.memory;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.*;

public class InMemoryUserRepository implements IUserRepository {
    private final List<User> users = new ArrayList<>();
    private final Map<String, User> loginIndex = new HashMap<>();

    public InMemoryUserRepository() {
        User admin = new User(Role.ADMIN, "Admin");
        users.add(admin);
        loginIndex.put(admin.getLogin().toLowerCase(), admin);
    }

    @Override
    public User add(User user) {
        users.add(user);
        loginIndex.put(user.getLogin().toLowerCase(), user);
        return user;
    }

    @Override
    public Optional<User> find(String login) {
        if (login == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(loginIndex.get(login.toLowerCase()));
    }
}
