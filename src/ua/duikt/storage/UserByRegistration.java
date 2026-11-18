package ua.duikt.storage;

import java.util.Comparator;

/** Критерій «дата реєстрації»; при однаковій даті — за логіном. */
public final class UserByRegistration implements Comparator<User> {

    @Override
    public int compare(User a, User b) {
        int byDate = a.registered().compareTo(b.registered());
        return byDate != 0 ? byDate : a.login().compareTo(b.login());
    }
}
