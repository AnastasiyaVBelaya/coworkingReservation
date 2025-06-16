package repository.file;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileUserRepository extends AbstractFileRepository<User> implements IUserRepository {

    private final Map<String, User> loginIndex;
    private static final String ADMIN_LOGIN = "Admin";

    public FileUserRepository() {
        super("data/users.ser");
        this.loginIndex = items.stream()
                .collect(Collectors.toMap(
                        user -> user.getLogin().toLowerCase(),
                        user -> user
                ));
        ensureAdminExists();
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
                .anyMatch(this::isAdmin);
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private void ensureAdminExists() {
        if (!adminExists()) {
            User admin = new User(Role.ADMIN, ADMIN_LOGIN);
            items.add(admin);
            loginIndex.put(admin.getLogin().toLowerCase(), admin);
            writeToFile();
        }
    }
}
