package ua.duikt.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Завантаження файлу (UTF-8, перший рядок — заголовок) у будь-яке сховище. */
public final class FileLoader {

    private FileLoader() { }

    /** @return кількість рядків з даними у файлі (без заголовка) */
    public static <K, T extends Keyed<K>> int loadInto(Path file, RecordFactory<T> factory, Storage<K, T> target)
            throws IOException {
        int lines = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();                                  // заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                lines++;
                target.add(factory.create(line.split(";", -1)));
            }
        }
        return lines;
    }
}
