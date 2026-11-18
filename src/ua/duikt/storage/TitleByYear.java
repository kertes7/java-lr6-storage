package ua.duikt.storage;

import java.util.Comparator;

/** Критерій «рік виходу»; при однаковому році — за ключем (детермінований порядок). Клас запису не змінюється. */
public final class TitleByYear implements Comparator<Title> {

    @Override
    public int compare(Title a, Title b) {
        int byYear = Integer.compare(a.year(), b.year());
        return byYear != 0 ? byYear : a.id().compareTo(b.id());
    }
}
