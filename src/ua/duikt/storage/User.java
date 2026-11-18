package ua.duikt.storage;

import java.time.LocalDate;

/** Користувач сервісу: ключ — логін. */
public record User(String login, String fullName, LocalDate registered) implements Keyed<String> {

    @Override
    public String key() { return login; }

    @Override
    public String toString() {
        return login + " " + fullName + ", зареєстровано " + registered;
    }
}
