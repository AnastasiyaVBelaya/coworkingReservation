package repository;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements IUserRepository {
    private final List<User> users = new ArrayList<>();

    public InMemoryUserRepository() {
        users.add(new User(Role.ADMIN, "Admin"));
    }

    @Override
    public User add(User user) {
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> find(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }
}
