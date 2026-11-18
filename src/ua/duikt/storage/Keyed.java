package ua.duikt.storage;

/** Запис, що має ключ. Єдина вимога сховища до типу запису. */
public interface Keyed<K> {

    K key();
}
