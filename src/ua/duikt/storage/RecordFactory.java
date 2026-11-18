package ua.duikt.storage;

/** Перетворення розібраного рядка файлу (поля) на запис. Реалізується окремо для кожного типу записів. */
public interface RecordFactory<T> {

    T create(String[] fields);
}
