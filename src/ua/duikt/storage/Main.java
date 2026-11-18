package ua.duikt.storage;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/** Завантажує обидва файли варіанта 0 в ОДНЕ й те саме узагальнене сховище. Без lambda і Stream API. */
public class Main {

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Path titlesFile = Path.of(args.length > 0 ? args[0] : "data/v0_titles.txt");
        Path usersFile = Path.of(args.length > 1 ? args[1] : "data/v0_users.txt");

        // ---------- тайтли ----------
        Storage<String, Title> titles = new Storage<>();
        int titleLines = FileLoader.loadInto(titlesFile, new RecordFactory<Title>() {
            @Override
            public Title create(String[] f) {
                return new Title(f[0], f[1], f[2], Integer.parseInt(f[3]), Double.parseDouble(f[4]));
            }
        }, titles);
        out.println("Тайтли: рядків у файлі " + titleLines + ", записів у сховищі " + titles.size()
                + " (повторів відхилено: " + titles.duplicatesRejected() + ")");
        for (String c : titles.conflicts()) out.println("  Конфлікт повтору: " + c);

        out.println("Пошук T-1017: " + describe(titles.find("T-1017")));
        out.println("Пошук T-0000: " + describe(titles.find("T-0000")));

        List<Title> byYear = titles.sortedBy(new TitleByYear());
        out.println("За роком виходу (" + byYear.size() + " записів):");
        for (Title t : byYear) out.println("  " + t.year() + " " + t.name() + "  [" + t.id() + "]");
        out.println("Перевірка повноти: усі записи сховища є у відсортованому результаті — "
                + (byYear.size() == titles.size() && new HashSet<>(byYear).equals(new HashSet<>(titles.view()))));

        // ---------- користувачі (те саме сховище, інший тип запису) ----------
        out.println();
        Storage<String, User> users = new Storage<>();
        int userLines = FileLoader.loadInto(usersFile, new RecordFactory<User>() {
            @Override
            public User create(String[] f) {
                return new User(f[0], f[1], LocalDate.parse(f[2]));
            }
        }, users);
        out.println("Користувачі: рядків у файлі " + userLines + ", записів у сховищі " + users.size()
                + " (повторів відхилено: " + users.duplicatesRejected() + ")");
        for (String c : users.conflicts()) out.println("  Конфлікт повтору: " + c);

        out.println("Пошук echo53: " + describe(users.find("echo53")));
        out.println("Пошук nobody: " + describe(users.find("nobody")));

        List<User> byDate = users.sortedBy(new UserByRegistration());
        out.println("За датою реєстрації (" + byDate.size() + " записів):");
        for (User u : byDate) out.println("  " + u.registered() + " " + u.login() + "  " + u.fullName());
        out.println("Перевірка повноти: усі записи сховища є у відсортованому результаті — "
                + (byDate.size() == users.size() && new HashSet<>(byDate).equals(new HashSet<>(users.view()))));

        // ---------- захист внутрішнього стану + for-each ----------
        out.println();
        int counter = 0;
        for (Title t : titles) counter++;                       // обхід звичайним for-each
        out.println("for-each по сховищу тайтлів: пройдено " + counter + " записів");
        try {
            titles.view().clear();                              // спроба змінити вміст в обхід методів
        } catch (UnsupportedOperationException e) {
            out.println("Спроба titles.view().clear() → " + e.getClass().getSimpleName() + " (сховище не змінено, розмір "
                    + titles.size() + ")");
        }
        try {
            java.util.Iterator<Title> it = titles.iterator();
            it.next();
            it.remove();
        } catch (UnsupportedOperationException e) {
            out.println("Спроба видалити через iterator().remove() → " + e.getClass().getSimpleName());
        }
    }

    private static String describe(Optional<? extends Keyed<String>> found) {
        return found.isPresent() ? found.get().toString() : "не знайдено";
    }
}
