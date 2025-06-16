package repository.file;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileUserRepository extends AbstractFileRepository<User> implements IUserRepository {

    private final Map<String, User> loginIndex;

    public FileUserRepository() {
        super("data/users.ser");
        this.loginIndex = new HashMap<>();
        for (User user : items) {
            loginIndex.put(user.getLogin().toLowerCase(), user);
        }
        if (!adminExists()) {
            User admin = new User(Role.ADMIN, "Admin");
            items.add(admin);
            loginIndex.put(admin.getLogin().toLowerCase(), admin);
            writeToFile();
        }
    }

    @Override
    public User add(User user) {
        if (user == null || user.getLogin() == null) {
            throw new IllegalArgumentException("User or login cannot be null");
        }
        items.add(user);
        loginIndex.put(user.getLogin().toLowerCase(), user);
        writeToFile();
        return user;
    }

    @Override
    public Optional<User> find(String login) {
        if (login == null || login.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(loginIndex.get(login.toLowerCase()));
    }

    private boolean adminExists() {
        return items.stream()
                .anyMatch(user -> user.getRole() == Role.ADMIN);
    }
}
