package ua.duikt.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Узагальнене сховище записів із ключем.
 * <ul>
 *   <li>LinkedHashMap: пошук за ключем за O(1) без перебору + стабільний порядок додавання;</li>
 *   <li>повтор ключа не створює дубліката: перший запис зберігається, повтор відхиляється
 *       (а якщо його дані відрізняються — фіксується як конфлікт);</li>
 *   <li>відсортовані результати будуються як КОПІЇ списків (List.sort стабільний), тому записи
 *       з однаковим значенням критерію не губляться (на відміну від TreeSet/TreeMap з компаратором);</li>
 *   <li>зовнішній код отримує лише подання для читання; for-each працює.</li>
 * </ul>
 */
public final class Storage<K, T extends Keyed<K>> implements Iterable<T> {

    private final Map<K, T> records = new LinkedHashMap<>();
    private final List<String> conflicts = new ArrayList<>();
    private int duplicatesRejected;

    /** @return true, якщо запис додано; false, якщо ключ уже є (повтор відхилено) */
    public boolean add(T record) {
        K key = record.key();
        T existing = records.get(key);
        if (existing != null) {
            duplicatesRejected++;
            if (!existing.equals(record)) {
                conflicts.add("ключ " + key + ": " + existing + " ≠ " + record);
            }
            return false;
        }
        records.put(key, record);
        return true;
    }

    /** Пошук за ключем без перебору записів. Відсутній ключ → Optional.empty(), а не null. */
    public Optional<T> find(K key) {
        return Optional.ofNullable(records.get(key));
    }

    public int size() { return records.size(); }

    public int duplicatesRejected() { return duplicatesRejected; }

    /** Розбіжні повтори (однаковий ключ, різні дані) — у вигляді копії-списку. */
    public List<String> conflicts() { return List.copyOf(conflicts); }

    /** Записи, відсортовані за заданим критерієм (компаратор передається ззовні). */
    public List<T> sortedBy(Comparator<? super T> comparator) {
        List<T> copy = new ArrayList<>(records.values());   // копія: сховище не змінюється
        copy.sort(comparator);                              // стабільне сортування: усі записи на місці
        return copy;
    }

    /** Подання лише для читання: зміни ззовні неможливі (UnsupportedOperationException). */
    public Collection<T> view() {
        return Collections.unmodifiableCollection(records.values());
    }

    @Override
    public Iterator<T> iterator() {
        return view().iterator();       // remove() через цей ітератор також заборонено
    }
}
