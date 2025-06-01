package repository.file;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.Optional;

public class FileUserRepository extends AbstractFileRepository<User> implements IUserRepository {

    public FileUserRepository() {
        super("data/users.ser");
        if (!adminExists()) {
            items.add(new User(Role.ADMIN, "Admin"));
            writeToFile();
        }
    }

    @Override
    public User add(User user) {
        items.add(user);
        writeToFile();
        return user;
    }

    @Override
    public Optional<User> find(String login) {
        return items.stream()
                .filter(user -> user.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    private boolean adminExists() {
        return items.stream()
                .anyMatch(user -> user.getRole() == Role.ADMIN);
    }
}
