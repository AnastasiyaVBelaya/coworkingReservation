package repository.entity;

import model.Role;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String login;
    private final Role role;

    public User(Role role, String login) {
        this.role = role;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format("User{login='%s', role=%s}", login, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return login.equalsIgnoreCase(user.login);
    }

    @Override
    public int hashCode() {
        return login.toLowerCase().hashCode();
    }
}
