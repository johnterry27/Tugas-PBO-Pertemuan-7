package org.yourcompany.yourproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private final Map<Long, Book> bookMap = new HashMap<>();
    private long nextId = 1;

    public List<Book> getAll() {
        return new ArrayList<>(bookMap.values());
    }

    public Book get(long id) {
        return bookMap.get(id);
    }

    public Book add(Book book) {
        book.setId(nextId++);
        bookMap.put(book.getId(), book);
        return book;
    }

    public Book update(long id, Book book) {
        book.setId(id);
        bookMap.put(id, book);
        return book;
    }

    public boolean delete(long id) {
        return bookMap.remove(id) != null;
    }
}
