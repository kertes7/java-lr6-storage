package ua.duikt.storage;

/** Тайтл стрімінгового сервісу. record: equals/hashCode за ВСІМА полями генеруються автоматично. */
public record Title(String id, String name, String type, int year, double rating) implements Keyed<String> {

    @Override
    public String key() { return id; }

    @Override
    public String toString() {
        return id + " " + name + ", " + year + ", рейтинг " + rating;
    }
}
